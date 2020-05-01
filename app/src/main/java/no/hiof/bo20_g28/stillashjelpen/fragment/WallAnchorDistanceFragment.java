package no.hiof.bo20_g28.stillashjelpen.fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.WallActivity;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

public class WallAnchorDistanceFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    private Button coverNoneButton, coverNetButton, coverTarpButton, forceFactorNormalButton, forceFactorParallelButton, saveAnchorDistanceButton;
    private EditText anchorForceEditText, bayLengthEditText;
    private TextView resultTextView, scaffoldHeightLabelTextView;
    private SeekBar scaffoldHeightSeekBar;

    private static int unselectedColor = Color.parseColor("#9A9A9A");
    private static int selectedColor = Color.parseColor("#FF3DA8D8");

    private static enum cover {
        UNCOVERED,
        NET,
        TARP
    }
    private static enum forceFactor {
        NORMAL,
        PARALLEL
    }


    private cover selectedCover;
    private forceFactor selectedForceFactor;
    private double anchorForce;
    private int scaffoldHeight;
    private double bayLength;

    private Wall thisWall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall_anchor_distance, container, false);

        // Finding views
        saveAnchorDistanceButton = view.findViewById(R.id.saveAnchorDistanceButton);
        saveAnchorDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { updateWallWithAnchorDistance(); }
        });
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
        startCoverButtons();
        startForceFactorButtons();
        startAnchorForceEditText();
        startScaffoldHeightSeekBar();
        startBayLengthEditText();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }

    private void startCoverButtons(){
        resetCoverButtonColors();
        //Setting onclick functions
        coverNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectedCover = cover.UNCOVERED;
                resetCoverButtonColors();
                coverNoneButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation(); }
        });
        coverNetButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectedCover = cover.NET;
                resetCoverButtonColors();
                coverNetButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation();}
        });
        coverTarpButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectedCover = cover.TARP;
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

    private void startForceFactorButtons(){
        // Setting button colors
        resetForceFactorButtonColors();

        //Setting onclick functions
        forceFactorNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedForceFactor = forceFactor.NORMAL;
                resetForceFactorButtonColors();
                forceFactorNormalButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation(); }
        });
        forceFactorParallelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedForceFactor = forceFactor.PARALLEL;
                resetForceFactorButtonColors();
                forceFactorParallelButton.setBackgroundColor(selectedColor);
                updateAnchorDistanceCalculation();}
        });
    }


    private void startAnchorForceEditText() {
        anchorForceEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    anchorForce = Double.parseDouble(s.toString());
                    updateAnchorDistanceCalculation();
                }
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
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateAnchorDistanceCalculation();
            }
        });
    }


    private void startBayLengthEditText(){
        bayLengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
//                    bayLength = Integer.parseInt(s.toString());
                    bayLength = Double.parseDouble((s.toString()));
                    updateAnchorDistanceCalculation();
                }
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
        String result = String.format("%.2f", calculateAnchorDistance());
//        resultTextView.setText("Resultat:\n Vertikal avstand mellom forankringer:\n Maksimum " + result + " cm");
        resultTextView.setText(Html.fromHtml("Resultat:<br><br>Vertikal avstand mellom forankringer:<br><font color=blue>" + result +
                " cm</font>"
        ));
    }

    private double calculateAnchorDistance(){
        double constructionFactor = 0;
        double powerFactor = 0;
        double densityFactor = 0;
        double velocityPressure = 0;

        // finding constructing factor (cs konstruksjonsfaktor)
        if(selectedCover == cover.UNCOVERED && selectedForceFactor == forceFactor.NORMAL)
            constructionFactor = 0.75;
        else constructionFactor = 1.0;

        //finding power factor (cf kraftfaktor)
        if(selectedCover != cover.UNCOVERED && selectedForceFactor == forceFactor.PARALLEL)
            powerFactor = 0.1;
        else powerFactor = 1.3;

        //finding density factor (tetthetsfaktor)
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

        // finding velocity factor (q1 hastighetstrykk)
        velocityPressure = 12.5 * scaffoldHeight + 800;

        // cs x cf x faglengde x tetthetsfaktor x q1 x 0.7 / Fw
        return (constructionFactor * powerFactor * bayLength * densityFactor * velocityPressure * 0.7) / (anchorForce / 1.2);
    }

    private void updateWallWithAnchorDistance() {
        thisWall.setWallAnchorDistance(calculateAnchorDistance());
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("walls");
        DatabaseReference wallRef = fDatabase.child(thisWall.getWallId());
        wallRef.setValue(thisWall);
    }
}
