package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.WallActivity;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;
import no.hiof.bo20_g28.stillashjelpen.model.Wall.Cover;
import no.hiof.bo20_g28.stillashjelpen.model.Wall.ForceFactor;

import static no.hiof.bo20_g28.stillashjelpen.WallActivity.scaffoldingName;

public class WallAnchorDistanceFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    private Button coverNoneButton, coverNetButton, coverTarpButton, forceFactorNormalButton, forceFactorParallelButton, saveAnchorDistanceButton, showCalculationButton;
    private EditText anchorForceEditText, bayLengthEditText;
    private TextView resultTextView, scaffoldHeightLabelTextView, BayLengthDescriptionTextView;
    private SeekBar scaffoldHeightSeekBar;

    private static int colorUnselectedButton = Color.parseColor("#9A9A9A");
    private static int colorSelectedButton = Color.parseColor("#FF3DA8D8");




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
            thisWall.setScaffoldType(scaffoldingName);
            saveAnchorDistanceButton.setVisibility(View.INVISIBLE);
            getPresetInputsFromScaffoldingSystem();
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

        //setting preset values from scaffold system if it's the first time the wall is used
        if(thisWall.isFirstTimeCreatingAnchorDistance())getPresetInputsFromScaffoldingSystem();
        //setting values from the last time the wall was used
        else setVariablesAndInputsFromWall(thisWall);
        thisWall.setFirstTimeCreatingAnchorDistance(false);
        updateAnchorDistanceCalculation();

        return view;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }

    private void setVariablesAndInputsFromWall(Wall wall){
        // setting variables
        selectedCover = wall.getCover();
        selectedForceFactor = wall.getForceFactor();
        anchorForce = wall.getAnchorForce();
        scaffoldHeight = wall.getScaffoldHeight();
        bayLength = wall.getBayLength();

        //updating views
        setCoverButtonColors();
        setForceFactorButtonColors();
        if(anchorForce > 0) anchorForceEditText.setText(String.format(Locale.ENGLISH,"%f", anchorForce));
        if(scaffoldHeight > 0) {
            scaffoldHeightSeekBar.setProgress(scaffoldHeight - 1);
            scaffoldHeightLabelTextView.setText(scaffoldHeight + " m");
        }
        if(bayLength > 0) bayLengthEditText.setText(String.format(Locale.ENGLISH,"%f", bayLength));
    }

    private void setPresetInputsFromScaffoldingSystem(ScaffoldingSystem ss){
        setCoverButtonColors();
        setForceFactorButtonColors();
        if(ss.getBayLength() > 0) bayLength = ss.getBayLength();
        if(thisWall.getBayLength() > 0) bayLength = thisWall.getBayLength();
        if(ss.getMaxHeight() > 0) scaffoldHeightSeekBar.setMax(ss.getMaxHeight());
        if(bayLength > 0) bayLengthEditText.setText(String.format(Locale.ENGLISH, "%f", bayLength));
        scaffoldHeight = 1;
        updateAnchorDistanceCalculation();
    }

    private void setCoverButtonColors(){
        /*coverNoneButton.setBackgroundColor(colorUnselectedButton);
        coverNetButton.setBackgroundColor(colorUnselectedButton);
        coverTarpButton.setBackgroundColor(colorUnselectedButton);*/

        coverNoneButton.setBackgroundResource(R.drawable.border);
        coverNetButton.setBackgroundResource(R.drawable.border);
        coverTarpButton.setBackgroundResource(R.drawable.border);

        coverNoneButton.setTextColor(getResources().getColor(R.color.colorAccent));
        coverNetButton.setTextColor(getResources().getColor(R.color.colorAccent));
        coverTarpButton.setTextColor(getResources().getColor(R.color.colorAccent));

        if(selectedCover == null) return;

        switch (selectedCover) {
            case UNCOVERED:
                coverNoneButton.setBackgroundResource(R.drawable.border_filled);
                coverNoneButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case NET:
                coverNetButton.setBackgroundResource(R.drawable.border_filled);
                coverNetButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case TARP:
                coverTarpButton.setBackgroundResource(R.drawable.border_filled);
                coverTarpButton.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void setForceFactorButtonColors(){
        forceFactorNormalButton.setBackgroundResource(R.drawable.border);
        forceFactorParallelButton.setBackgroundResource(R.drawable.border);

        forceFactorNormalButton.setTextColor(getResources().getColor(R.color.colorAccent));
        forceFactorParallelButton.setTextColor(getResources().getColor(R.color.colorAccent));

        if(selectedForceFactor == null) return;

        switch (selectedForceFactor) {
            case NORMAL:
                forceFactorNormalButton.setBackgroundResource(R.drawable.border_filled);
                forceFactorNormalButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case PARALLEL:
                forceFactorParallelButton.setBackgroundResource(R.drawable.border_filled);
                forceFactorParallelButton.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void coverButtonsListener(){
        //Setting onclick functions
        coverNoneButton.setOnClickListener(v -> {
            selectedCover = Cover.UNCOVERED;
            //coverNoneButton.setBackgroundColor(colorSelectedButton);
            setCoverButtonColors();
            if(selectedForceFactor == ForceFactor.PARALLEL) {
                BayLengthDescriptionTextView.setText(R.string.fagbredde);
            } else{
                BayLengthDescriptionTextView.setText(R.string.faglengde);
            }
            updateAnchorDistanceCalculation(); });

        coverNetButton.setOnClickListener(v -> {
            selectedCover = Cover.NET;
            //coverNetButton.setBackgroundColor(colorSelectedButton);
            setCoverButtonColors();
            BayLengthDescriptionTextView.setText(R.string.faglengde);
            updateAnchorDistanceCalculation();});

        coverTarpButton.setOnClickListener(v -> {
            selectedCover = Cover.TARP;
            //coverTarpButton.setBackgroundColor(colorSelectedButton);
            setCoverButtonColors();
            BayLengthDescriptionTextView.setText(R.string.faglengde);
            updateAnchorDistanceCalculation();});
    }

    private void forceFactorButtonsListener(){
        //Setting onclick functions
        forceFactorNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BayLengthDescriptionTextView.setText(R.string.faglengde);
                selectedForceFactor = ForceFactor.NORMAL;
                setForceFactorButtonColors();
                updateAnchorDistanceCalculation(); }
        });
        forceFactorParallelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedCover == Cover.UNCOVERED) {
                    BayLengthDescriptionTextView.setText(R.string.fagbredde);
                } else{
                    BayLengthDescriptionTextView.setText(R.string.faglengde);
                }
                selectedForceFactor = ForceFactor.PARALLEL;
                setForceFactorButtonColors();
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
                scaffoldHeightLabelTextView.setText(text + " m");
                scaffoldHeight = progress + 1;
                updateAnchorDistanceCalculation();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        scaffoldHeightSeekBar.setOnTouchListener(new SeekBar.OnTouchListener()
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
                " m</font>"));
        WallActivity.wallAnchorDistance = getCalculateAnchorDistance();
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

    private float getCalculateAnchorDistance(){
        setConstructionFactor();
        setPowerFactor();
        setDensityFactor();
        setVelocityPressure();
        //  Fw / cs x cf x faglengde x tetthetsfaktor x q1 x 0.7
        return (float) ((anchorForce / 1.2) / (constructionFactor * powerFactor * bayLength * densityFactor * velocityPressure * 0.7));
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


    private void updateWallWithAnchorDistance() {
        thisWall.setWallAnchorDistance(getCalculateAnchorDistance());
        thisWall.setCover(selectedCover);
        thisWall.setForceFactor(selectedForceFactor);
        thisWall.setAnchorForce(anchorForce);
        thisWall.setScaffoldHeight(scaffoldHeight);
        thisWall.setBayLength(bayLength);
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
//            return ((anchorForce / 1.2) / (constructionFactor * powerFactor * bayLength * densityFactor * velocityPressure * 0.7));
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
