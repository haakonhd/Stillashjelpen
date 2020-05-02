package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.WallActivity;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem.Cover;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem.ForceFactor;

public class WallAnchorDistanceFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    private Button coverNoneButton, coverNetButton, coverTarpButton, forceFactorNormalButton, forceFactorParallelButton, saveAnchorDistanceButton, showCalculationButton;
    private EditText anchorForceEditText, bayLengthEditText;
    private TextView resultTextView, scaffoldHeightLabelTextView, BayLengthDescriptionTextView;
    private SeekBar scaffoldHeightSeekBar;

    private static int unselectedColor = Color.parseColor("#9A9A9A");
    private static int selectedColor = Color.parseColor("#FF3DA8D8");




    private Cover selectedCover;
    private ForceFactor selectedForceFactor;
    private double anchorForce;
    private int scaffoldHeight;
    private double bayLength;
    private double constructionFactor = 0;
    private double powerFactor = 0;
    private double densityFactor = 0;
    private double velocityPressure = 0;

    private Wall thisWall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall_anchor_distance, container, false);

        // Finding views
        saveAnchorDistanceButton = view.findViewById(R.id.saveAnchorDistanceButton);
        saveAnchorDistanceButton.setOnClickListener(v -> updateWallWithAnchorDistance());
        showCalculationButton = view.findViewById(R.id.showCalculationButton);
        showCalculationButton.setOnClickListener(v -> { openCalculationDialog(); });
        coverNoneButton = view.findViewById(R.id.coverNoneButton);
        coverNetButton = view.findViewById(R.id.coverNetButton);
        coverTarpButton = view.findViewById(R.id.coverTarpButton);
        forceFactorNormalButton = view.findViewById(R.id.forceFactorNormalButton);
        forceFactorParallelButton = view.findViewById(R.id.forceFactorParallelButton);
        anchorForceEditText = view.findViewById(R.id.anchorForceEditText);
        bayLengthEditText = view.findViewById(R.id.bayLengthEditText);
        resultTextView = view.findViewById(R.id.resultTextView);
        scaffoldHeightLabelTextView = view.findViewById(R.id.scaffoldHeightLabelTextView);
        scaffoldHeightSeekBar = view.findViewById(R.id.scaffoldHeightSeekBar);
        BayLengthDescriptionTextView = view.findViewById(R.id.bayLengthDescriptionTextView);
        scaffoldHeight = 1;

        if(WallActivity.isQuickCalculation){
            thisWall = new Wall();
            thisWall.setScaffoldType("none");
            saveAnchorDistanceButton.setVisibility(View.INVISIBLE);
        }
        if(!WallActivity.isQuickCalculation){
            Intent i = getActivity().getIntent();
            thisWall = (Wall) i.getSerializableExtra("passedWall");
            saveAnchorDistanceButton.setVisibility(View.VISIBLE);
        }
        coverButtonsListener();
        forceFactorButtonsListener();
        anchorForceEditTextListener();
        startScaffoldHeightSeekBar();
        bayLengthEditTextListener();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }
//    private cover selectedCover;
//    private forceFactor selectedForceFactor;
//    private double anchorForce;
//    private int scaffoldHeight;
//    private double bayLength;
//    private double constructionFactor = 0;
//    private double powerFactor = 0;
//    private double densityFactor = 0;
//    private double velocityPressure = 0;

    private void setPresetInputsFromScaffoldingSystem(ScaffoldingSystem ss){
        /*selectedCover = getLoadClassLoad(ss.getScaffoldLoadClass());

        bayLength = ss.getBayLength();
        bayWidth = ss.getBayWidth();
        weight = ss.getWeight();

        loadClassSeekBar.setProgress(ss.getScaffoldLoadClass() - 1);
        bayLengthEditText.setText(String.valueOf(bayLength));
        bayWidthEditText.setText(String.valueOf(bayWidth));
        weightEditText.setText(String.valueOf(weight));
         */
    }

    private void coverButtonsListener(){
        resetCoverButtonColors();
        //Setting onclick functions
        coverNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectedCover = Cover.UNCOVERED;
                resetCoverButtonColors();
                coverNoneButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation(); }
        });
        coverNetButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectedCover = Cover.NET;
                resetCoverButtonColors();
                coverNetButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation();}
        });
        coverTarpButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectedCover = Cover.TARP;
                resetCoverButtonColors();
                coverTarpButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation();}
        });
    }

    private void resetCoverButtonColors(){
        coverNoneButton.setBackgroundColor(unselectedColor);
        coverNetButton.setBackgroundColor(unselectedColor);
        coverTarpButton.setBackgroundColor(unselectedColor);
    }

    private void resetForceFactorButtonColors(){
        forceFactorNormalButton.setBackgroundColor(unselectedColor);
        forceFactorParallelButton.setBackgroundColor(unselectedColor);
    }

    private void forceFactorButtonsListener(){
        // Setting button colors
        resetForceFactorButtonColors();

        //Setting onclick functions
        forceFactorNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BayLengthDescriptionTextView.setText(R.string.faglengde);
                selectedForceFactor = ForceFactor.NORMAL;
                resetForceFactorButtonColors();
                forceFactorNormalButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation(); }
        });
        forceFactorParallelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BayLengthDescriptionTextView.setText(R.string.fagbredde);
                selectedForceFactor = ForceFactor.PARALLEL;
                resetForceFactorButtonColors();
                forceFactorParallelButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation();}
        });
    }


    private void anchorForceEditTextListener() {
        anchorForceEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) anchorForce = Double.parseDouble(s.toString());
                else anchorForce = 0;
                updateAnchorDistanceCalculation();
            }
        });
    }

    private void startScaffoldHeightSeekBar(){
        scaffoldHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.valueOf(progress + 1);
                scaffoldHeightLabelTextView.setText(text);
                scaffoldHeight = progress + 1;
                updateAnchorDistanceCalculation();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }


    private void bayLengthEditTextListener(){
        bayLengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0)  bayLength = Double.parseDouble((s.toString()));
                else bayLength = 0;
                updateAnchorDistanceCalculation();
            }
        });
    }

    private boolean inputsArefilled(){
        return(
            selectedCover != null &&
            selectedForceFactor!= null &&
            anchorForce > 0 &&
            scaffoldHeight > 0 &&
            bayLength > 0
        );
    }

    private void updateAnchorDistanceCalculation(){
        if(!inputsArefilled()) {
            resultTextView.setText("Fyll ut alle felter for å regne ut maksimal avstand mellom forankringer");
            return;
        }
        String result = String.format("%.2f", getCalculateAnchorDistance());
//        resultTextView.setText("Resultat:\n Vertikal avstand mellom forankringer:\n Maksimum " + result + " cm");
        resultTextView.setText(Html.fromHtml("Resultat:<br><br>Vertikal avstand mellom forankringer:<br><font color=blue>" + result +
                " m</font>"
        ));
    }

    // finding cosnstruction factor (cs konstruksjonsfaktor)
    private void setConstructionFactor(){
        if(selectedCover == Cover.UNCOVERED && selectedForceFactor == ForceFactor.NORMAL)
            constructionFactor = 0.75;
        else constructionFactor = 1.0;
    }

    //finding power factor (cf kraftfaktor)
    private void setPowerFactor(){
        if(selectedCover != Cover.UNCOVERED && selectedForceFactor == ForceFactor.PARALLEL)
            powerFactor = 0.1;
        else powerFactor = 1.3;
    }

    //finding density factor (tetthetsfaktor)
    private void setDensityFactor(){
        switch (selectedCover) {
            case UNCOVERED:
                densityFactor = 0.2;
                break;
            case NET:
                densityFactor = 0.5;
                break;
            case TARP:
                densityFactor = 1;
                break;
        }
    }

    // finding velocity factor (q1 hastighetstrykk)
    private void setVelocityPressure(){
        double velocityPressureInNewton = (12.5 * scaffoldHeight) + 800;
        velocityPressure = velocityPressureInNewton / 1000;
    }

    private double getCalculateAnchorDistance(){
        setConstructionFactor();
        setPowerFactor();
        setDensityFactor();
        setVelocityPressure();
        // cs x cf x faglengde x tetthetsfaktor x q1 x 0.7 / Fw
        return (anchorForce / 1.2) / (constructionFactor * powerFactor * bayLength * densityFactor * velocityPressure * 0.7);
    }

    private void updateWallWithAnchorDistance() {
        if(!inputsArefilled()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Lagre");
            final TextView textView = new TextView(getActivity());
            textView.setPadding(20,20,20,20);
            textView.setText("Vennligst fyll ut alle felter");
            textView.setTextSize(20);
            builder.setView(textView);

            builder.setNegativeButton("Lukk", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return;
        }
        thisWall.setWallAnchorDistance(getCalculateAnchorDistance());
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
        wallRef.setValue(thisWall);
    }

    private void openCalculationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kalkulasjon");
        final TextView textView = new TextView(getActivity());
        textView.setPadding(40,20,40,20);
        if(inputsArefilled()) {
            textView.setText(Html.fromHtml("Formel: <br><div style='text-align:center;'> " +
                    "<u>c</u><sub>s</sub> <u>x c</u><sub>f</sub><u> x faglengde x tetthetsfaktor x q</u><sub>1</sub><u> x 0,7</u>" +
                    "<br>" +
                    "F<sub>w</sub>" +
                    "</div>" +
                    "Utregning: <br><div style='text-align:center;'><u>"+
                    constructionFactor + " x " + powerFactor + " x " + bayLength + " x " + densityFactor + " x " + velocityPressure + " x 0.7 </u><br>" +
                    "(" + anchorForce + " x 1.2)</div>"
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
