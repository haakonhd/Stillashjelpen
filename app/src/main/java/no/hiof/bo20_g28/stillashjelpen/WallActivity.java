package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WallActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private ImageView wallImageView;
    private Wall thisWall;
    private TextView wallNameTextView;
    private TextView soleBoardAreaTextView;
    private TextView wallAnchorDistanceTextView;
    private TextView wallDescriptionTextView;
    private String currentPhotoPath;
    private Uri imageUri;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        Intent i = getIntent();
        thisWall = (Wall) i.getSerializableExtra("passedWall");

        wallImageView = findViewById(R.id.wallImageView);
        wallNameTextView = findViewById(R.id.wallNameTextView);
        soleBoardAreaTextView = findViewById(R.id.soleBoardAreaTextView);
        wallAnchorDistanceTextView = findViewById(R.id.wallAnchorDistanceTextView);
        wallDescriptionTextView = findViewById(R.id.wallDescriptionTextView);

        wallNameTextView.setText(thisWall.getWallName());
        soleBoardAreaTextView.setText("Underplankareal: " + thisWall.getSoleBoardArea());
        wallAnchorDistanceTextView.setText("Forankringsavstand: " + thisWall.getWallAnchorDistance());
        wallDescriptionTextView.setText(thisWall.getWallDescription());

        progressDialog = new ProgressDialog(this);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");

        if(thisWall.getPictureId() != null) {
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.placeholder(R.drawable.ic_domain_black_24dp);

            GlideApp.with(this)
                    .asBitmap()
                    .load(storageReference.child(thisWall.getPictureId()))
                    .apply(options)
                    .into(wallImageView);
        }

    }

    public void cameraImageButtonClicked(View view) {
        //takePic();
        takePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            File f = new File(currentPhotoPath);

            uploadImageToFirebase(f.getName());
        }
    }

    private void addPhotoToImageView(Bitmap bitmap) {
        RequestOptions options = new RequestOptions();

        GlideApp.with(this)
                .asBitmap()
                .load(bitmap)
                .apply(options)
                .into(wallImageView);
    }

    private void uploadImageToFirebase(final String fileName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference image = storageRef.child("images").child(fileName);
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

        final Bitmap formattedBitmap = imageFormating(bitmap);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        formattedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        progressDialog.setMessage("Lagrer bilde...");
        progressDialog.show();

        image.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        progressDialog.dismiss();
                        deleteWallsOldPicture();
                        updateWallWithImage(fileName);
                        Toast.makeText(getApplicationContext(), "Bilde lagret", Toast.LENGTH_SHORT).show();
                        addPhotoToImageView(formattedBitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Lagring mislyktes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Bitmap imageFormating(Bitmap bitmap){
        Bitmap imageBitmap = null;
        try {
            imageBitmap = rotateImageIfRequired(this, bitmap, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap scaledBitmap;
        if (imageBitmap.getWidth() < imageBitmap.getHeight())
            scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, 600, 800, false);
        else
            scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, 800, 600, false);
        // Crop the picture to fit a square
        Bitmap croppedBitmap;
        if (scaledBitmap.getWidth() > scaledBitmap.getHeight())
            croppedBitmap = Bitmap.createBitmap(scaledBitmap, scaledBitmap.getWidth() / 2 - scaledBitmap.getHeight() / 2, 0, 600, 600);
        else
            croppedBitmap = Bitmap.createBitmap(scaledBitmap, 0, scaledBitmap.getHeight() / 2 - scaledBitmap.getWidth() / 2, 600, 600);

        return croppedBitmap;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    //Rotation crash fix. Saves necessary globals that onRestoreInstanceState needs to restore activity if orientation changes while in "camera mode"
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("pic", imageBitmap);
        outState.putString("currentPhotoPath", currentPhotoPath);
        outState.putParcelable("imageUri", imageUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPhotoPath = savedInstanceState.getString("currentPhotoPath");
        imageUri = savedInstanceState.getParcelable("imageUri");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "no.hiof.bo20_g28.stillashjelpen.fileprovider", photoFile);
                imageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void deleteWallsOldPicture(){
        if(thisWall.getPictureId() != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
            StorageReference imageReference = storageReference.child(thisWall.getPictureId());
            imageReference.delete();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    //------------------------Button Click Handling-------------------------------------------------


    public void editWallNameImageButtonClicked(View view) {
        editWallNameDialogbox();
    }

    public void editWallSoleBoardAreaImageButtonClicked(View view) {
        /*Intent i = new Intent(this, CalculationActivity.class);
        i.putExtra("passedWall", thisWall);
        i.putExtra("from", "wallSoleBoardArea");
        startActivity(i);*/
        Toast.makeText(getApplicationContext(),"editWallSoleBoardAreaImageButton clicked", Toast.LENGTH_SHORT).show();
    }

    public void editWallAnchorDistanceImageButtonClicked(View view) {
        /*Intent i = new Intent(this, CalculationActivity.class);
        i.putExtra("passedWall", thisWall);
        i.putExtra("from", "anchorDistance");
        startActivity(i);*/
        Toast.makeText(getApplicationContext(),"editWallAnchorDistanceImageButton clicked", Toast.LENGTH_SHORT).show();
    }

    public void editWallDescriptionImageButtonClicked(View view) {
        editWallDescriptionDialogbox();
    }


    //------------------------Dialog boxes----------------------------------------------------------


    private void editWallNameDialogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nytt veggnavn");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(thisWall.getWallName());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateWallWithNewName(input.getText().toString());
                wallNameTextView.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void editWallDescriptionDialogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Legg til beskrivelse");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(thisWall.getWallDescription());

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateWallWithDescription(input.getText().toString());
                wallDescriptionTextView.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    //------------------------Update Firebase-------------------------------------------------------


    private void updateWallWithImage(String name) {
        thisWall.setPictureId(name);
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
        wallRef.setValue(thisWall);
    }

    private void updateWallWithNewName(String wallName) {
        thisWall.setWallName(wallName);
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
        wallRef.setValue(thisWall);
    }

    private void updateWallWithDescription(String wallDescription) {
        thisWall.setWallDescription(wallDescription);
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
        wallRef.setValue(thisWall);
    }

    /*public void takePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/
}
