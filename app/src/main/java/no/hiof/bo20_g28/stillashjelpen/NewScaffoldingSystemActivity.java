package no.hiof.bo20_g28.stillashjelpen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;

public class NewScaffoldingSystemActivity extends AppCompatActivity {

    private EditText newScaffoldSystemNameEditText, newBayLengthEditText, newBayWidthEditText, newMaxHeightEditText;
    private SeekBar newLoadClassSeekBar;
    private TextView newLoadClassLabelTextView, newSsTitleTextView;
    private ImageButton deleteScaffoldSystemImageButton;

    private final List<String> scaffoldingSystemList = new ArrayList<String>();

    private String name = "";
    private double bayLength = 0;
    private double bayWidth = 0;
    private int maxHeight = 0;
    private int loadClass = 1;

    private ScaffoldingSystem thisScaffoldSystem;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scaffolding_system);

        getScaffoldingSystemsFromFirebase();

        Intent i = getIntent();
        thisScaffoldSystem = (ScaffoldingSystem) i.getSerializableExtra("passedScaffoldSystem");
        from = i.getStringExtra("from");

        newScaffoldSystemNameEditText = findViewById(R.id.newScaffoldSystemNameEditText);
        newSsTitleTextView = findViewById(R.id.newSsTitleTextView);
        newBayLengthEditText = findViewById(R.id.newBayLenghtEditText);
        newBayWidthEditText = findViewById(R.id.newBayWidthEditText);
        newMaxHeightEditText = findViewById(R.id.newMaxHeightEditText);
        newLoadClassSeekBar = findViewById(R.id.newLoadClassSeekBar);
        newLoadClassLabelTextView = findViewById(R.id.newLoadClassLabelTextView);
        deleteScaffoldSystemImageButton = findViewById(R.id.deleteScaffoldSystemImageButton);
        deleteScaffoldSystemImageButton.setVisibility(View.INVISIBLE);

        startNewLoadClassSeekBar();

        if(from.equals("old")){
            setPresetInputs();
            deleteScaffoldSystemImageButton.setVisibility(View.VISIBLE);
            newSsTitleTextView.setText("Endre stillassystem");
        }
        if(from.equals("new")){
            deleteScaffoldSystemImageButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setPresetInputs(){
        newScaffoldSystemNameEditText.setText(thisScaffoldSystem.getScaffoldingSystemName());
        newBayLengthEditText.setText(String.format(Locale.ENGLISH, "%f", thisScaffoldSystem.getBayLength()));
        newBayWidthEditText.setText(String.format(Locale.ENGLISH, "%f", thisScaffoldSystem.getBayWidth()));
        newMaxHeightEditText.setText(String.format(Locale.ENGLISH, "%d", thisScaffoldSystem.getMaxHeight()));
        newLoadClassSeekBar.setProgress(thisScaffoldSystem.getScaffoldLoadClass() - 1);
        newLoadClassLabelTextView.setText(String.format(Locale.ENGLISH, "Klasse %d",thisScaffoldSystem.getScaffoldLoadClass()));
        loadClass = thisScaffoldSystem.getScaffoldLoadClass();
    }

    public void saveNewScaffoldSystemButtonClicked(View view) {
        getInput();
    }

    public void deleteScaffoldSystemImageButtonClicked(View view) {
        deleteScaffoldSystemDialogBox(thisScaffoldSystem.getScaffoldingSystemId());
    }

    private void getInput(){
        if(inputsAreFilled()){
            name = newScaffoldSystemNameEditText.getText().toString().trim();
            bayLength = Double.parseDouble(newBayLengthEditText.getText().toString().trim());
            bayWidth = Double.parseDouble(newBayWidthEditText.getText().toString().trim());
            maxHeight = Integer.parseInt(newMaxHeightEditText.getText().toString().trim());

            if(from.equals("new")) {
                if (checkIfNameIsNotInUse(name)) {
                    saveScaffoldingSystem(name, bayLength, bayWidth, loadClass, maxHeight);

                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra(from, "scaffoldSystem");
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Navnet er allerede i bruk", Toast.LENGTH_SHORT).show();
                }
            }else{
                if(from.equals("old")){
                    updateScaffoldingSystem(name, bayLength, bayWidth, loadClass, maxHeight);

                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra(from, "scaffoldSystem");
                    startActivity(i);
                }
            }
        }else{
            Toast.makeText(getApplicationContext(), "Fyll inn alle feltene", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean inputsAreFilled(){
        String n = newScaffoldSystemNameEditText.getText().toString().trim();
        String bl = newBayLengthEditText.getText().toString().trim();
        String bw = newBayWidthEditText.getText().toString().trim();
        String mh = newMaxHeightEditText.getText().toString().trim();

        if(n.length() > 0 && bl.length() > 0 && bw.length() > 0 && mh.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean checkIfNameIsNotInUse(String name){
        for(int i = 0; i < scaffoldingSystemList.size(); i++){
            if(name.equals(scaffoldingSystemList.get(i))){
                return false;
            }
        }
        return true;
    }

    private void getScaffoldingSystemsFromFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference().child("scaffoldingSystems");

        scaffoldingSystemList.clear();

        fDatabaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    String ss = addressSnapshot.child("scaffoldingSystemName").getValue(String.class);
                    if (ss != null) {
                        scaffoldingSystemList.add(ss);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FirebaseError", databaseError.toException());
            }
        });
    }

    private void saveScaffoldingSystem(String name, double bayLength, double bayWidth, int scaffoldLoadClass, int maxHeight){
        ScaffoldingSystem ss = new ScaffoldingSystem();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scaffoldingSystemsRef = databaseRef.child("scaffoldingSystems").push();

        ss.setScaffoldingSystemId(scaffoldingSystemsRef.getKey());
        ss.setScaffoldingSystemName(name);
        ss.setBayLength(bayLength);
        ss.setBayWidth(bayWidth);
        ss.setMaxHeight(maxHeight);
        ss.setScaffoldLoadClass(scaffoldLoadClass);

        scaffoldingSystemsRef.setValue(ss);

        Toast.makeText(getApplicationContext(), "Nytt stillassystem er registrert", Toast.LENGTH_SHORT).show();
    }

    private void updateScaffoldingSystem(String name, double bayLength, double bayWidth, int scaffoldLoadClass, int maxHeight) {

        thisScaffoldSystem.setScaffoldingSystemName(name);
        thisScaffoldSystem.setBayLength(bayLength);
        thisScaffoldSystem.setBayWidth(bayWidth);
        thisScaffoldSystem.setMaxHeight(maxHeight);
        thisScaffoldSystem.setScaffoldLoadClass(scaffoldLoadClass);

        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("scaffoldingSystems");
        DatabaseReference scaffoldingSystemsRef = fDatabase.child(thisScaffoldSystem.getScaffoldingSystemId());
        scaffoldingSystemsRef.setValue(thisScaffoldSystem);

        Toast.makeText(getApplicationContext(), "Stillassystemet er oppdatert", Toast.LENGTH_SHORT).show();
    }

    private void startNewLoadClassSeekBar(){
        newLoadClassSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String nr = String.valueOf(progress + 1);
                newLoadClassLabelTextView.setText("Klasse " + nr);
                loadClass = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user
                // stops touching the SeekBar
            }
        });

        // preventing screen from being dragged when adjusting seeker
        newLoadClassSeekBar.setOnTouchListener(new SeekBar.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle Seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    private void deleteScaffoldSystemDialogBox(final String scaffoldSystemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Slette stillassystemet \"" + thisScaffoldSystem.getScaffoldingSystemName() + "\"?");

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteScaffoldSystemFromFireBase(scaffoldSystemId);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("from", "scaffoldSystem");
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Stillassystemet er slettet", Toast.LENGTH_SHORT).show();
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

    private void deleteScaffoldSystemFromFireBase(String scaffoldId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference wallRef = databaseReference.child("scaffoldingSystems");

        wallRef.child(scaffoldId).removeValue();
    }
}
