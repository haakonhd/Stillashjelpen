package no.hiof.bo20_g28.stillashjelpen;

import androidx.appcompat.app.AppCompatActivity;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WallActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private ImageView wallImageView;
    private Wall thisWall;
    private TextView wallNameTextView;
    private TextView soleBoardAreaTextView;
    private TextView wallAnchorDistanceTextView;
    private TextView wallDescriptionTextView;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        Intent i = getIntent();
        thisWall = (Wall) i.getSerializableExtra("passedWall");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        wallImageView = findViewById(R.id.wallImageView);
        wallNameTextView = findViewById(R.id.wallNameTextView);
        soleBoardAreaTextView = findViewById(R.id.soleBoardAreaTextView);
        wallAnchorDistanceTextView = findViewById(R.id.wallAnchorDistanceTextView);
        wallDescriptionTextView = findViewById(R.id.wallDescriptionTextView);

        wallNameTextView.setText(thisWall.getWallName());
        soleBoardAreaTextView.setText("Underplankareal: " + thisWall.getSoleBoardArea());
        wallAnchorDistanceTextView.setText("Forankringsavstand: " + thisWall.getWallAnchorDistance());
        wallDescriptionTextView.setText(thisWall.getWallDescription());
    }


    public void takePic(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            wallImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("pic", imageBitmap);
        super.onSaveInstanceState(outState);
    }

    public void cameraImageButtonClicked(View view) {
        takePic(view);
    }

}
