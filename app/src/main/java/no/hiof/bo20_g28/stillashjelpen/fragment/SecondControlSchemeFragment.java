package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

public class SecondControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    private EditText csLengthEditText, csWidthEditText, csHeightEditText, csNumOfWallAnchorsEditText,
            csNumOfWallAnchorTestsEditText, csWallAnchorHoldsEditText, csWallAnchorTestResultEditText;

    private Button typeFacadeButton, typeTowerButton, typeRollerButton, typeOtherButton, saveTechnicalDataButton;

    private String scaffoldType = "";

    private View view;

    private static int unselectedColor = Color.parseColor("#9A9A9A");
    private static int selectedColor = Color.parseColor("#FF3DA8D8");

    private Project thisProject;
    private ControlScheme thisCs;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_second, container, false);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");
        thisCs = thisProject.getControlScheme();

        bindAllViews(view);
        setOnClicks();

        fillInPresetControlSchemeValuesForThisProject();


        return view;
    }

    private void fillInPresetControlSchemeValuesForThisProject() {
        if(thisCs.getScaffoldType() != null) {
            if (thisCs.getScaffoldType().equals("Facade")) {
                updateTypeButtons(typeFacadeButton);
            }
            if (thisCs.getScaffoldType().equals("Tower")) {
                updateTypeButtons(typeTowerButton);
            }
            if (thisCs.getScaffoldType().equals("Roller")) {
                updateTypeButtons(typeRollerButton);
            }
            if (thisCs.getScaffoldType().equals("Other")) {
                updateTypeButtons(typeOtherButton);
            }
        }
        csLengthEditText.setText(thisCs.getScaffoldLength());
        csWidthEditText.setText(thisCs.getScaffoldWidth());
        csHeightEditText.setText(thisCs.getScaffoldHeight());
        csNumOfWallAnchorsEditText.setText(thisCs.getNumOfWallAnchors());
        csNumOfWallAnchorTestsEditText.setText(thisCs.getNumOfWallAnchorTests());
        csWallAnchorHoldsEditText.setText(thisCs.getWallAnchorHolds());
        csWallAnchorTestResultEditText.setText(thisCs.getWallAnchorTestResult());
    }

    public void save(){
        thisCs.setScaffoldType(scaffoldType);
        thisCs.setScaffoldLength(csLengthEditText.getText().toString().trim());
        thisCs.setScaffoldWidth(csWidthEditText.getText().toString().trim());
        thisCs.setScaffoldHeight(csHeightEditText.getText().toString().trim());
        thisCs.setNumOfWallAnchors(csNumOfWallAnchorsEditText.getText().toString().trim());
        thisCs.setNumOfWallAnchorTests(csNumOfWallAnchorTestsEditText.getText().toString().trim());
        thisCs.setWallAnchorHolds(csWallAnchorHoldsEditText.getText().toString().trim());
        thisCs.setWallAnchorTestResult(csWallAnchorTestResultEditText.getText().toString().trim());

        updateThisProjectsControlScheme();

        Toast.makeText(getContext(), "Kontroll-skjemaet er oppdatert",Toast.LENGTH_LONG).show();
    }

    public void updateTypeButtons(View view){
        if(view.getId() == typeFacadeButton.getId()) {
            scaffoldType = "Facade";
            resetCoverButtonColors();
            typeFacadeButton.setBackgroundResource(R.drawable.border_filled);
            typeFacadeButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //typeFacadeButton.setBackgroundColor(selectedColor);
        }
        if(view.getId() == typeTowerButton.getId()) {
            scaffoldType = "Tower";
            resetCoverButtonColors();
            typeTowerButton.setBackgroundResource(R.drawable.border_filled);
            typeTowerButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //typeTowerButton.setBackgroundColor(selectedColor);
        }
        if(view.getId() == typeRollerButton.getId()) {
            scaffoldType = "Roller";
            resetCoverButtonColors();
            typeRollerButton.setBackgroundResource(R.drawable.border_filled);
            typeRollerButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //typeRollerButton.setBackgroundColor(selectedColor);
        }
        if(view.getId() == typeOtherButton.getId()) {
            scaffoldType = "Other";
            resetCoverButtonColors();
            typeOtherButton.setBackgroundResource(R.drawable.border_filled);
            typeOtherButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //typeOtherButton.setBackgroundColor(selectedColor);
        }
    }

    private void resetCoverButtonColors(){
        /*typeFacadeButton.setBackgroundColor(unselectedColor);
        typeTowerButton.setBackgroundColor(unselectedColor);
        typeRollerButton.setBackgroundColor(unselectedColor);
        typeOtherButton.setBackgroundColor(unselectedColor);*/
        typeFacadeButton.setBackgroundResource(R.drawable.border);
        typeTowerButton.setBackgroundResource(R.drawable.border);
        typeRollerButton.setBackgroundResource(R.drawable.border);
        typeOtherButton.setBackgroundResource(R.drawable.border);

        typeFacadeButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        typeTowerButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        typeRollerButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        typeOtherButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public void updateThisProjectsControlScheme(){
        thisProject.setControlScheme(thisCs);
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("projects");
        DatabaseReference projectRef = fDatabase.child(thisProject.getProjectId());
        projectRef.setValue(thisProject);
    }

    private void bindAllViews(View view) {
        csLengthEditText = view.findViewById(R.id.csLengthEditText);
        csWidthEditText = view.findViewById(R.id.csWidthEditText);
        csHeightEditText = view.findViewById(R.id.csHeightEditText);
        csNumOfWallAnchorsEditText = view.findViewById(R.id.csNumOfWallAnchorsEditText);
        csNumOfWallAnchorTestsEditText = view.findViewById(R.id.csNumOfWallAnchorTestsEditText);
        csWallAnchorHoldsEditText = view.findViewById(R.id.csWallAnchorHoldsEditText);
        csWallAnchorTestResultEditText = view.findViewById(R.id.csWallAnchorTestResultEditText);

        typeFacadeButton = view.findViewById(R.id.typeFacadeButton);
        typeTowerButton = view.findViewById(R.id.typeTowerButton);
        typeRollerButton = view.findViewById(R.id.typeRollerButton);
        typeOtherButton = view.findViewById(R.id.typeOtherButton);
        saveTechnicalDataButton = view.findViewById(R.id.saveTechnicalDataButton);
    }

    private void setOnClicks() {
        typeFacadeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTypeButtons(v);
            }
        });
        typeTowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTypeButtons(v);
            }
        });
        typeRollerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTypeButtons(v);
            }
        });
        typeOtherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTypeButtons(v);
            }
        });
        saveTechnicalDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }
}
