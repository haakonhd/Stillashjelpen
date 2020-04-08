package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            File f = new File(currentPhotoPath);
            wallImageView.setImageURI(Uri.fromFile(f));

            uploadImageToFirebase(f.getName());
            updateWallWithImage(f.getName());
        }
    }

    private void updateWallWithImage(String name) {
        thisWall.setPictureId(name);
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
        wallRef.setValue(thisWall);
    }

    private void uploadImageToFirebase(String fileName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference image = storageRef.child("images").child(fileName);
        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
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

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        image.putBytes(imageData);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("pic", imageBitmap);
        super.onSaveInstanceState(outState);
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

    private void dispatchTakePictureIntent() {
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }




    /*public void takePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/
}
