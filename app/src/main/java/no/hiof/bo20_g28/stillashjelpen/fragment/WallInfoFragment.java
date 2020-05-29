package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.GlideApp;
import no.hiof.bo20_g28.stillashjelpen.ProjectActivity;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.WallActivity;
import no.hiof.bo20_g28.stillashjelpen.model.Project;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class WallInfoFragment extends Fragment{
    public static final String ARG_OBJECT = "object";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private ImageView wallImageView;
    private Wall thisWall;
    private Project thisProject;
    private TextView wallNameTextView;
    private TextView soleBoardAreaTextView;
    private TextView soleBoardAreaOuterTextView;
    private TextView wallAnchorDistanceTextView;
    private TextView wallDescriptionTextView;
    private String currentPhotoPath;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private ImageButton cameraImageButton;
    private ImageButton editWallNameImageButton;
    private ImageButton editWallDescriptionImageButton;
    private ImageButton deleteWallImageButton;
    private View view;
    private boolean connectedToFirebase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wall_info, container, false);

        Intent i = getActivity().getIntent();
        thisWall = (Wall) i.getSerializableExtra("passedWall");
        thisProject = (Project) i.getSerializableExtra("passedProject");

        getConnectionState();

        wallImageView = view.findViewById(R.id.wallImageView);
        wallNameTextView = view.findViewById(R.id.wallNameTextView);
        soleBoardAreaTextView = view.findViewById(R.id.soleBoardAreaTextView);
        soleBoardAreaOuterTextView = view.findViewById(R.id.soleBoardAreaOuterTextView);
        wallAnchorDistanceTextView = view.findViewById(R.id.wallAnchorDistanceTextView);
        wallDescriptionTextView = view.findViewById(R.id.wallDescriptionTextView);

        wallNameTextView.setText(thisWall.getWallName());
        soleBoardAreaTextView.setText("Underplankareal: " + thisWall.getSoleBoardArea());
        String stringResult = String.format("%.2f", thisWall.getWallAnchorDistance());
        wallAnchorDistanceTextView.setText(Html.fromHtml("Forankringsavstand: " +  stringResult));
        if(thisWall.getWallDescription() != null) {
            wallDescriptionTextView.setTypeface(Typeface.DEFAULT);
            wallDescriptionTextView.setText(thisWall.getWallDescription());
        }
        progressDialog = new ProgressDialog(getContext());

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

        cameraImageButton = (ImageButton) view.findViewById(R.id.cameraImageButton);
        cameraImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cameraImageButtonClicked();
            }
        });

        editWallNameImageButton = (ImageButton) view.findViewById(R.id.editWallNameImageButton);
        editWallNameImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editWallNameImageButtonClicked();
            }
        });

        editWallDescriptionImageButton = (ImageButton) view.findViewById(R.id.editWallDescriptionImageButton);
        editWallDescriptionImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editWallDescriptionImageButtonClicked();
            }
        });

        deleteWallImageButton = (ImageButton) view.findViewById(R.id.deleteWallmageButton);
        deleteWallImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteWallImageButtonClicked();
            }
        });

        startListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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

        final Bitmap formattedBitmap = imageFormatting(bitmap);

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
                Toast.makeText(getContext(), "Bilde lagret", Toast.LENGTH_LONG).show();
                addPhotoToImageView(formattedBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Lagring mislyktes", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Bitmap imageFormatting(Bitmap bitmap){
        Bitmap imageBitmap = null;
        try {
            imageBitmap = rotateImageIfRequired(getContext(), bitmap, imageUri);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("pic", imageBitmap);
        outState.putString("currentPhotoPath", currentPhotoPath);
        outState.putParcelable("imageUri", imageUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString("currentPhotoPath");
            imageUri = savedInstanceState.getParcelable("imageUri");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "no.hiof.bo20_g28.stillashjelpen.fileprovider", photoFile);
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
    public void onResume() {
        super.onResume();
        if(WallActivity.wallAnchorDistance > 0)
            wallAnchorDistanceTextView.setText("Maksimal forankringsavstand: " + String.format("%.2f", WallActivity.wallAnchorDistance) + " m");
        else
            wallAnchorDistanceTextView.setText("Maksimal forankringsavstand: - ");
        if(WallActivity.soleBoardArea > 0) {
            soleBoardAreaTextView.setText(Html.fromHtml("Underlagsplank-areal for innerspir: " + WallActivity.soleBoardArea + " <font>cm<sup><small>2</small></sup></font>"));
            soleBoardAreaOuterTextView.setText(Html.fromHtml("Underlagsplank-areal for ytterspir: " + WallActivity.soleBoardArea/2 + " <font>cm<sup><small>2</small></sup></font>"));
        }
        else{
            soleBoardAreaTextView.setText(Html.fromHtml("Underlagsplank-areal for innerspir: - "));
            soleBoardAreaOuterTextView.setText(Html.fromHtml("Underlagsplank-areal for ytterspir: - "));
        }
    }

    private void getConnectionState() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    connectedToFirebase = true;
                } else {
                    connectedToFirebase = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });
    }


    //------------------------Button Click Handling-------------------------------------------------

    private void cameraImageButtonClicked() {
        getConnectionState();
        if(connectedToFirebase) {
            takePictureIntent();
        }else{
            Toast.makeText(getActivity(), "Applikasjonen er ikke koblet til databasen. Ikke mulig å ta bilder", Toast.LENGTH_LONG).show();
        }
    }

    private void editWallNameImageButtonClicked() {
        editWallNameDialogbox();
    }

    private void editWallDescriptionImageButtonClicked() {
        editWallDescriptionDialogbox();
    }

    private void deleteWallImageButtonClicked() {
        deleteWallDialogbox(thisWall.getWallId());
    }


    //------------------------Dialog boxes----------------------------------------------------------

    private void editWallNameDialogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nytt veggnavn");

        // Set up the input
        final EditText input = new EditText(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Legg til beskrivelse");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(thisWall.getWallDescription());

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateWallWithDescription(input.getText().toString());
                wallDescriptionTextView.setTypeface(Typeface.DEFAULT);
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

    private void deleteWallDialogbox(final String wallId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Slette veggen \"" + thisWall.getWallName() + "\"?");

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getContext(), ProjectActivity.class);
                i.putExtra("passedProject", thisProject);
                i.putExtra("from", "deleteWall");
                i.putExtra("deleteWallId", thisWall.getWallId());
                startActivity(i);
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

    private void startListeners() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference wallSoleAreaRef = database.child("walls").child(thisWall.getWallId()).child("soleBoardArea");
        DatabaseReference wallAnchorDistanceRef = database.child("walls").child(thisWall.getWallId()).child("wallAnchorDistance");

        ValueEventListener soleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String area = dataSnapshot.getValue().toString();
                    int areaInt = Integer.parseInt(area);

                    if(areaInt > 0) {
                        soleBoardAreaTextView.setText(Html.fromHtml("Underlagsplank-areal for innerspir: " + areaInt + " <font>cm<sup><small>2</small></sup></font>"));
                        soleBoardAreaOuterTextView.setText(Html.fromHtml("Underlagsplank-areal for ytterspir: " + areaInt /2 + " <font>cm<sup><small>2</small></sup></font>"));
                    }
                    else{
                        soleBoardAreaTextView.setText(Html.fromHtml("Underlagsplank-areal for innerspir: - "));
                        soleBoardAreaOuterTextView.setText(Html.fromHtml("Underlagsplank-areal for ytterspir: - "));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        wallSoleAreaRef.addValueEventListener(soleListener);

        ValueEventListener anchorListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String maxDistanceString = dataSnapshot.getValue().toString();
                    double maxDistance = Double.parseDouble(maxDistanceString);

                    if(maxDistance > 0) wallAnchorDistanceTextView.setText("Maksimal forankringsavstand: " + String.format("%.2f", maxDistance) + " m");
                    else wallAnchorDistanceTextView.setText("Maksimal forankringsavstand: - ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        wallAnchorDistanceRef.addValueEventListener(anchorListener);
    }

}
