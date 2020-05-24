package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.WallActivity;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

public class SoleBoardAreaFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    private Button saveSoleBoardAreaButton, showCalculationButton;
    private TextView resultTextView, nrOfFloorsLabelTextView, loadClassLabelTextView, bayLengthMeterLabelTextView, kgLabelTextView, bayWidthMeterLabelTextView,
            nrOfFloorsDescriptionTextView, bayLengthDescriptionTextView, bayWidthDescriptionTextView, loadClassDescriptionTextView, kNLabelTextView, groundDescriptionTextView;
    private EditText weightEditText, bayLengthEditText, bayWidthEditText, groundKiloNewtonEditText;
    private Spinner groundSpinner;
    private SeekBar loadClassSeekBar, floorSeekBar;

    private double groundKiloNewton;// 500
    private int load; //75
    private int nrOfFloors; //1
    private double bayLength; // 3
    private double bayWidth; //0.7
    private int weight; //800

    private int groundSpinnerPosition;
    private int loadSeekbarPosition;
    private final List<Integer> groundKNList = new ArrayList<>();

    private Wall thisWall;
    private int soleBoardArea;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soleboard_tab, container, false);

        saveSoleBoardAreaButton = view.findViewById(R.id.saveSoleBoardAreaButton);
        saveSoleBoardAreaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveSoleBoardAreaButtonClicked();
            }
        });

        showCalculationButton = view.findViewById(R.id.showCalculationButton);
        showCalculationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openCalculationDialog();
            }
        });

        weightEditText = view.findViewById(R.id.weightEditText);
        bayLengthEditText = view.findViewById(R.id.bayLengthEditText);
        bayWidthEditText = view.findViewById(R.id.bayWidthEditText);
        groundKiloNewtonEditText = view.findViewById(R.id.groundKiloNewtonEditText);
        groundSpinner = view.findViewById(R.id.groundSpinner);
        loadClassSeekBar = view.findViewById(R.id.loadClassSeekBar);
        floorSeekBar = view.findViewById(R.id.floorSeekBar);
        resultTextView = view.findViewById(R.id.resultTextView);
        nrOfFloorsLabelTextView = view.findViewById(R.id.nrOfFloorsLabelTextView);
        loadClassLabelTextView = view.findViewById(R.id.loadClassLabelTextView);
        kNLabelTextView = view.findViewById(R.id.kNLabelTextView);

        if(WallActivity.isQuickCalculation){
            thisWall = new Wall();
            thisWall.setScaffoldType("none");
            saveSoleBoardAreaButton.setVisibility(View.INVISIBLE);
        }
        if(!WallActivity.isQuickCalculation){
            Intent i = getActivity().getIntent();
            thisWall = (Wall) i.getSerializableExtra("passedWall");
            saveSoleBoardAreaButton.setVisibility(View.VISIBLE);
        }

        setKnLabelTextViewText();
        startSpinner();
        startNrOfFloorsSeekBar();
        startLoadClassSeekBar();

        groundKiloNewtonEditTextListener();
        bayLengthEditTextListener();
        bayWidthEditTextListener();
        weightEditTextListener();

        //setting preset values from scaffold system if it's the first time the wall is used
        if(thisWall.isFirstTimeCreatingSoleBoardArea()) getPresetInputsFromScaffoldingSystem();
        //setting values from the last time the wall was used
        else setVariablesAndInputsFromWall(thisWall);
        thisWall.setFirstTimeCreatingSoleBoardArea(false);
        updateSoleBoardCalculation();

        return view;
    }

    private void setVariablesAndInputsFromWall(Wall wall){
        if(wall.getGroundKiloNewton() > 0) {
            groundKiloNewton = wall.getGroundKiloNewton();
            groundSpinnerPosition = wall.getGroundSpinnerPosition();
            groundSpinner.setSelection(groundSpinnerPosition);
            groundKiloNewtonEditText.setText(String.format(Locale.ENGLISH,"%d",groundKNList.get(groundSpinnerPosition)));
        }
        if(wall.getLoad() > 0) {
            load = wall.getLoad();
            loadSeekbarPosition = wall.getLoadSeekerPosition();
            loadClassSeekBar.setProgress(loadSeekbarPosition);
            loadClassLabelTextView.setText(String.format(Locale.ENGLISH,"Klasse %d", loadSeekbarPosition + 1));
        }
        if(wall.getBayLength() > 0) {
            bayLength = wall.getBayLength();
            bayLengthEditText.setText(String.format(Locale.ENGLISH, "%f", bayLength));
        }
        if(wall.getBayWidth() > 0) {
            bayWidth = wall.getBayWidth();
            bayWidthEditText.setText(String.format(Locale.ENGLISH, "%f", bayWidth));
        }
        if(wall.getNrOfFloors() > 0) {
            nrOfFloors = wall.getNrOfFloors();
            floorSeekBar.setProgress(nrOfFloors -1);
            nrOfFloorsLabelTextView.setText(String.format(Locale.ENGLISH, "%d",nrOfFloors));
        }
        if(wall.getWeight() > 0) {
            weight = wall.getWeight();
            weightEditText.setText(String.format(Locale.ENGLISH, "%d",weight));
        }

    }

    private void setKnLabelTextViewText(){
        kNLabelTextView.setText(Html.fromHtml("<font>kN/m<sup><small>2</small></sup></font>"));
    }

    private void startSpinner(){

        final List<String> groundList = new ArrayList<>();
        groundList.add("Grus og stein");
        groundList.add("Asfalt, standarisert");
        groundList.add("Grov sand, fast lagret");
        groundList.add("Asfalt");
        groundList.add("Fin sand, fast lagret");
        groundList.add("Fin sand, løst lagret");
        groundList.add("Leire");

        groundKNList.add(500);
        groundKNList.add(500);
        groundKNList.add(375);
        groundKNList.add(300);
        groundKNList.add(250);
        groundKNList.add(125);
        groundKNList.add(80);
        
        ArrayAdapter<String> scaffoldSystemAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, groundList);
        scaffoldSystemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groundSpinner.setAdapter(scaffoldSystemAdapter);
        groundSpinner.setSelection(0);

        groundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String text = String.valueOf(groundKNList.get(groundSpinner.getSelectedItemPosition()));
                groundKiloNewtonEditText.setText(text);
                groundSpinnerPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }

    private void startNrOfFloorsSeekBar(){
        floorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.valueOf(progress +1);
                nrOfFloorsLabelTextView.setText(text);
                nrOfFloors = progress + 1;
                updateSoleBoardCalculation();
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

        floorSeekBar.setOnTouchListener(new SeekBar.OnTouchListener()
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

    private void startLoadClassSeekBar(){
        loadClassSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.valueOf(progress + 1);
                loadClassLabelTextView.setText("Klasse " + text);
                load = getLoadClassLoad(progress + 1);
                loadSeekbarPosition = progress;
                updateSoleBoardCalculation();
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
        loadClassSeekBar.setOnTouchListener(new SeekBar.OnTouchListener()
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

    private int getLoadClassLoad(int loadClass){
        if(loadClass == 1){
            return 75;
        }
        if(loadClass == 2){
            return 150;
        }
        if(loadClass == 3){
            return 200;
        }
        if(loadClass == 4) {
            return 300;
        }
        if(loadClass == 5){
            return 450;
        }
        if(loadClass == 6){
            return 600;
        }
        else{
            return 600;
        }
    }

    private void groundKiloNewtonEditTextListener(){
        groundKiloNewtonEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if input exists
                if(s.length() > 0) groundKiloNewton = Integer.parseInt(s.toString());
                else groundKiloNewton = 0;
                updateSoleBoardCalculation();
            }
        });
    }

    private void bayLengthEditTextListener(){
        bayLengthEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if input exists
                if(s.length() > 0) {
//                    String lastChar = String.valueOf(s.charAt(s.length() - 1));
//                    if (!lastChar.equals(".")) {
                        bayLength = Double.parseDouble(s.toString());
//                    }
//                    else{
//                        bayLengthEditText.getText().clear();
//                        bayLength = 0;
//                    }
                }
                else bayLength = 0;

                updateSoleBoardCalculation();
            }
        });
    }

    private void bayWidthEditTextListener(){
        bayWidthEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if input exists
                if(s.length() > 0) {
//                    String lastChar = String.valueOf(s.charAt(s.length() - 1));
//                    if (!lastChar.equals("."))
                        bayWidth = Double.parseDouble(s.toString());
//                    else {
//                        bayWidthEditText.getText().clear();
//                        bayWidth = 0;
//                    }
                }
                else bayWidth = 0;
                updateSoleBoardCalculation();
            }
        });
    }

    private void weightEditTextListener(){
        weightEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if input exists
                if(s.length() > 0) weight = Integer.parseInt(s.toString());
                else weight = 0;

                updateSoleBoardCalculation();
            }
        });
    }

    private void getPresetInputsFromScaffoldingSystem(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference().child("scaffoldingSystems");

        fDatabaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot scaffoldingSystemsSnapshot : dataSnapshot.getChildren()) {
                    ScaffoldingSystem ss = scaffoldingSystemsSnapshot.getValue(ScaffoldingSystem.class);

                    if (ss.getScaffoldingSystemName().equals(thisWall.getScaffoldType())) {
                        setPresetInputsFromScaffoldingSystem(ss);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FirebaseError", databaseError.toException());
            }
        });
    }

    private void setPresetInputsFromScaffoldingSystem(ScaffoldingSystem ss){
        if(ss.getScaffoldLoadClass() > 0) {
            load = getLoadClassLoad(ss.getScaffoldLoadClass());
            loadClassSeekBar.setProgress(ss.getScaffoldLoadClass() - 1);
        }
        if(ss.getBayLength() > 0){
            bayLength = ss.getBayLength();
            bayLengthEditText.setText(String.valueOf(bayLength));
        }
        if(thisWall.getBayLength() > 0) {
            bayLength = thisWall.getBayLength();
            bayLengthEditText.setText(String.valueOf(bayLength));
        }
        if(ss.getBayWidth() > 0) {
            bayWidth = ss.getBayWidth();
            bayWidthEditText.setText(String.valueOf(bayWidth));
        }
        nrOfFloors = 1;
        if(ss.getWeight() > 0){
            weight = ss.getWeight();
            weightEditText.setText(String.valueOf(weight));
        }
    }

    private boolean inputsAreFilled(){
        return(
            load != 0 &&
            bayWidth != 0 &&
            bayLength != 0 &&
            nrOfFloors != 0 &&
            weight != 0 &&
            groundKiloNewton != 0
        );
    }

    private void updateSoleBoardCalculation(){
        if(!inputsAreFilled()){
            resultTextView.setText("Vennligst fyll ut alle feltene for å regne ut underlagsplank-areal");
            soleBoardArea = 0;
            return;
        }
        maxPayload = (float) (load * bayWidth * bayLength);
        totalPayload = maxPayload * nrOfFloors;
        totalLoad = totalPayload + weight;

        resultOuterSpear = totalLoad / 4;
        resultInnerSpear = totalLoad / 2;

        groundKiloPrCm2 = (float) (groundKiloNewton / 100);

        resultOuterSpearFinished = resultOuterSpear / groundKiloPrCm2;
        resultInnerSpearFinished = resultInnerSpear / groundKiloPrCm2;

        resultOuterSpearFinishedCeil = (int)Math.ceil(resultOuterSpearFinished);
        resultInnerSpearFinishedCeil = (int)Math.ceil(resultInnerSpearFinished);

        updateResultTextView(resultOuterSpearFinishedCeil, resultInnerSpearFinishedCeil);
        soleBoardArea = resultInnerSpearFinishedCeil;
        WallActivity.soleBoardArea = soleBoardArea;
    }

    private float maxPayload;
    private float totalPayload;
    private float totalLoad;
    private float resultOuterSpear;
    private float resultInnerSpear;
    private float groundKiloPrCm2;
    private float resultOuterSpearFinished;
    private float resultInnerSpearFinished;
    private int resultOuterSpearFinishedCeil;
    private int resultInnerSpearFinishedCeil;

    private void updateResultTextView(int outerResult, int innerResult){
        resultTextView.setText(Html.fromHtml("Resultat:<br><br>Ytterspir underlagsplank-areal:<br><font color=blue>" + outerResult +
                " cm<sup><small>2</small></sup></font><br><br>" + "Innerspir underlagsplank-areal:<br><font color=blue>" + innerResult + " cm<sup><small>2</small></sup></font>"));
    }

    private void saveSoleBoardAreaButtonClicked(){
        updateWallWithSoleBoardArea(soleBoardArea);
    }

    private void updateWallWithSoleBoardArea(int soleBoardArea) {

        thisWall.setSoleBoardArea(soleBoardArea);
        thisWall.setGroundKiloNewton(groundKiloNewton);
        thisWall.setGroundSpinnerPosition(groundSpinnerPosition);
        thisWall.setLoad(load);
        thisWall.setBayWidth(bayWidth);
        thisWall.setBayLength(bayLength);
        thisWall.setNrOfFloors(nrOfFloors);
        thisWall.setWeight(weight);
        thisWall.setLoadSeekerPosition(loadSeekbarPosition);

        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
        wallRef.setValue(thisWall);

        CharSequence text = "Utregning lagret";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getActivity(), text, duration);
        toast.show();
    }

    private void openCalculationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kalkulasjon");
        final TextView textView = new TextView(getActivity());
        textView.setPadding(40,20,40,20);
        if(inputsAreFilled()) {
            textView.setText(Html.fromHtml(
                    /*"Formel: <br><div style='text-align:center;'> " +
                    "Tillatt nyttelast =<br>Faglengde x Fagbredde x Nyttelast for belastningsklasse " + load + "<br><br>" +
                    "Total last pr. spir =<br><u>Egenvekt + (Tillatt nyttelast x antall platformer)</u><br>" +
                    "4<br><br>" +
                    "Underlangsplank areal =<br><u>Total last</u><br>" +
                    "Underlagets bærekraft" +
                    "</div>" +*/

                    "Utregning: <br><div style='text-align:center;'>"+
                    maxPayload + "kg = " + bayLength + "m x " + bayWidth + "m x " + load + "kg<br><br>" +
                    resultOuterSpear + "kg =  " + "<u>" + weight + "kg + (" + maxPayload + "kg x " + nrOfFloors + ")" + "</u><br>" +
                    "4<br><br>" +
                            resultOuterSpearFinishedCeil + "cm<sup><small>2</small></sup> =  <u>" + resultOuterSpear + "kg</u><br>" +
                            groundKiloPrCm2 + "kg/cm<sup><small>2</small></sup>" +
                    "</div>"
            ));
//            return (constructionFactor * powerFactor * bayLength * densityFactor * velocityPressure * 0.7) / (anchorForce / 1.2);
        }
        else{
            textView.setText("Vennligst fyll ut alle felter");
        }
        textView.setTextSize(20);
        builder.setView(textView);

        builder.setNegativeButton("Lukk", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
