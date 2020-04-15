package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import no.hiof.bo20_g28.stillashjelpen.adapter.TabCalculationAdapter;
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
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

    TabLayout tabLayout;
    ViewPager2 viewPager;


    private TabCalculationAdapter tabCalculationAdapter() {
        TabCalculationAdapter adapter = new TabCalculationAdapter(this);
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(tabCalculationAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(getTabTextFromPosition(position));
                        setTabIcon(position);
                    }
                }).attach();
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

    private String getTabTextFromPosition(int position){
        if(position == 0) return "Vegginformasjon";
        if(position == 1) return "Areal for underlagsplank";
        if(position == 2) return "Forankringsavstand";
        //TODO: handle error
        else return "error";
    }

    private void setTabIcon(int position){
        int[] tabIcons = {
                R.drawable.ic_wall_info,
                R.drawable.ic_wall_info,
                R.drawable.ic_wall_info
        };
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if(tab != null){
            tab.setIcon(tabIcons[position]);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

     */

    //------------------------Button Click Handling-------------------------------------------------
//        public void editWallNameImageButtonClicked(View view) {
//            editWallNameDialogbox();
//        }
//
//    public void editWallSoleBoardAreaImageButtonClicked(View view) {
//        /*Intent i = new Intent(this, CalculationActivity.class);
//        i.putExtra("passedWall", thisWall);
//        i.putExtra("from", "wallSoleBoardArea");
//        startActivity(i);*/
//        Toast.makeText(getApplicationContext(),"editWallSoleBoardAreaImageButton clicked", Toast.LENGTH_SHORT).show();
//    }


//    public void editWallAnchorDistanceImageButtonClicked(View view) {
//        /*Intent i = new Intent(this, CalculationActivity.class);
//        i.putExtra("passedWall", thisWall);
//        i.putExtra("from", "anchorDistance");
//        startActivity(i);*/
//        Toast.makeText(getApplicationContext(),"editWallAnchorDistanceImageButton clicked", Toast.LENGTH_SHORT).show();
//    }
//
//public void editWallDescriptionImageButtonClicked(View view) {
//        editWallDescriptionDialogbox();
//    }
//
//    //------------------------Dialog boxes----------------------------------------------------------
//
//
//    private void editWallNameDialogbox() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Nytt veggnavn");
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        input.setText(thisWall.getWallName());
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                updateWallWithNewName(input.getText().toString());
//                wallNameTextView.setText(input.getText().toString());
//            }
//        });
//        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }
//
//    private void editWallDescriptionDialogbox() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Legg til beskrivelse");
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        input.setText(thisWall.getWallDescription());
//
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                updateWallWithDescription(input.getText().toString());
//                wallDescriptionTextView.setText(input.getText().toString());
//            }
//        });
//        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }
//
//
//    //------------------------Update Firebase-------------------------------------------------------
//
//
//    private void updateWallWithImage(String name) {
//        thisWall.setPictureId(name);
//        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
//        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
//        wallRef.setValue(thisWall);
//    }
//
//    private void updateWallWithNewName(String wallName) {
//        thisWall.setWallName(wallName);
//        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
//        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
//        wallRef.setValue(thisWall);
//    }
//
//    private void updateWallWithDescription(String wallDescription) {
//        thisWall.setWallDescription(wallDescription);
//        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
//        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
//        wallRef.setValue(thisWall);
//    }
//
//    /*public void takePic() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }*/
}
