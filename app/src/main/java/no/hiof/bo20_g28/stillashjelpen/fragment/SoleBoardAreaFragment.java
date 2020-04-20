package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import no.hiof.bo20_g28.stillashjelpen.MainActivity;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

public class SoleBoardAreaFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    private Button saveSoleBoardAreaButton;
    private TextView resultTextView, nrOfFloorsLabelTextView, loadClassLabelTextView, bayLengthMeterLabelTextView, kgLabelTextView, bayWidthMeterLabelTextView,
            nrOfFloorsDescriptionTextView, bayLengthDescriptionTextView, bayWidthDescriptionTextView, loadClassDescriptionTextView, kNLabelTextView, groundDescriptionTextView;
    private EditText weightEditText, bayLengthEditText, bayWidthEditText, groundKiloNewtonEditText;
    private Spinner groundSpinner;
    private SeekBar loadClassSeekBar, floorSeekBar;

    private int load = 75;
    private int nrOfFloors = 1;
    private double groundKiloNewton = 500;
    private double bayLength = 3;
    private double bayWidth = 0.7;
    private int weight = 800;

    private Wall thisWall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soleboard_tab, container, false);

        Intent i = getActivity().getIntent();
        thisWall = (Wall) i.getSerializableExtra("passedWall");

        saveSoleBoardAreaButton = view.findViewById(R.id.saveSoleBoardAreaButton);
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

        startSpinner();
        startNrOfFloorsSeekBar();
        startLoadClassSeekBar();

        groundKiloNewtonEditTextListener();
        bayLengthEditTextListener();
        bayWidthEditTextListener();
        weightEditTextListener();

        getPresetInputsFromScaffoldingSystem();
        updateSoleBoardCalculation();


        return view;
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

        final List<Integer> groundKNList = new ArrayList<>();
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
    }

    private void startLoadClassSeekBar(){
        loadClassSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // When the progress value has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.valueOf(progress + 1);
                loadClassLabelTextView.setText("Klasse " + text);
                load = getLoadClassLoad(progress + 1);
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
                if(s.length() > 0) {
                    groundKiloNewton = Integer.parseInt(s.toString());
                    updateSoleBoardCalculation();
                }
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
                    String lastChar = String.valueOf(s.charAt(s.length() - 1));
                    if (!lastChar.equals(".")) {
                        bayLength = Float.parseFloat(s.toString());
                        updateSoleBoardCalculation();
                    }
                }
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
                    String lastChar = String.valueOf(s.charAt(s.length() - 1));
                    if (!lastChar.equals(".")) {
                        bayWidth = Float.parseFloat(s.toString());
                        updateSoleBoardCalculation();
                    }
                }
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
                if(s.length() > 0) {
                    weight = Integer.parseInt(s.toString());
                    updateSoleBoardCalculation();
                }
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
        load = getLoadClassLoad(ss.getScaffoldLoadClass());
        bayLength = ss.getBayLength();
        bayWidth = ss.getBayWidth();
        weight = ss.getWeight();

        loadClassSeekBar.setProgress(ss.getScaffoldLoadClass() - 1);
        bayLengthEditText.setText(String.valueOf(bayLength));
        bayWidthEditText.setText(String.valueOf(bayWidth));
        weightEditText.setText(String.valueOf(weight));
    }

    private void updateSoleBoardCalculation(){
        float result = (float) (load * bayWidth * bayLength);
        float result2 = result * nrOfFloors;
        float result3 = result2 + weight;

        float resultOuterSpear = result3 / 4;
        float resultInnerSpear = result3 / 2;

        float groundKiloPrCm2 = (float) (groundKiloNewton / 100);

        float resultOuterSpearFinished = resultOuterSpear / groundKiloPrCm2;
        float resultInnerSpearFinished = resultInnerSpear / groundKiloPrCm2;

        int resultOuterSpearFinishedCeil = (int)Math.ceil(resultOuterSpearFinished);
        int resultInnerSpearFinishedCeil = (int)Math.ceil(resultInnerSpearFinished);

        updateResultTextView(resultOuterSpearFinishedCeil, resultInnerSpearFinishedCeil);
    }

    private void updateResultTextView(int outerResult, int innerResult){
        resultTextView.setText(Html.fromHtml("Resultat:<br><br>Ytterspir underlagsplank-areal:<br><font color=blue>" + outerResult +
                " cm2</font><br><br>" + "Innerspir underlagsplank-areal:<br><font color=blue>" + innerResult + " cm2</font>"));
    }

}
