package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

public class FirstControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    private EditText builderCompanyEditText, refNumEditText, placeEditText, builtByEditText, userCompanyEditText, phoneEditText,
            contactPersonEditText, builderNameControlEditText, userNameControlEditText, builderControlDateEditText, userControlDateEditText;

    private View view;

    private Button saveCsButton;


    private Project thisProject;
    private ControlScheme thisCs;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_first, container, false);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");
        thisCs = thisProject.getControlScheme();

        bindAllViews(view);
        setOnClicks();

        fillInPresetControlSchemeValuesForThisProject();

        return view;
    }

    public void fillInPresetControlSchemeValuesForThisProject(){
        builderCompanyEditText.setText(thisCs.getBuilderCompanyName());
        refNumEditText.setText(thisCs.getRefNum());
        placeEditText.setText(thisCs.getPlaceName());
        builtByEditText.setText(thisCs.getBuiltByName());
        userCompanyEditText.setText(thisCs.getUserCompanyName());
        phoneEditText.setText(thisCs.getUserPhoneNum());
        contactPersonEditText.setText(thisCs.getContactPersonName());
        builderNameControlEditText.setText(thisCs.getBuilderNameControlName());
        userNameControlEditText.setText(thisCs.getUserNameControlName());
        builderControlDateEditText.setText(thisCs.getBuilderControlDate());
        userControlDateEditText.setText(thisCs.getUserControlDate());
    }

    public void updateThisProjectsControlScheme(){
        thisProject.setControlScheme(thisCs);
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("projects");
        DatabaseReference projectRef = fDatabase.child(thisProject.getProjectId());
        projectRef.setValue(thisProject);
    }

    public void save(){
        thisCs.setBuilderCompanyName(builderCompanyEditText.getText().toString().trim());
        thisCs.setRefNum(refNumEditText.getText().toString().trim());
        thisCs.setPlaceName(placeEditText.getText().toString().trim());
        thisCs.setBuiltByName(builtByEditText.getText().toString().trim());
        thisCs.setUserCompanyName(userCompanyEditText.getText().toString().trim());
        thisCs.setUserPhoneNum(phoneEditText.getText().toString().trim());
        thisCs.setContactPersonName(contactPersonEditText.getText().toString().trim());
        thisCs.setBuilderNameControlName(builderNameControlEditText.getText().toString().trim());
        thisCs.setUserNameControlName(userNameControlEditText.getText().toString().trim());
        thisCs.setBuilderControlDate(builderControlDateEditText.getText().toString().trim());
        thisCs.setUserControlDate(userControlDateEditText.getText().toString().trim());

        updateThisProjectsControlScheme();
        Toast.makeText(getContext(), "Kontroll-skjemaet er oppdatert",Toast.LENGTH_LONG).show();
    }

    public void openDatePickerDialog(final View v) {

        Calendar cal = Calendar. getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    switch (v.getId()) {
                        case R.id.builderControlDateEditText:
                            ((EditText)v).setText(selectedDate);
                            break;
                        case R.id.userControlDateEditText:
                            ((EditText)v).setText(selectedDate);
                            break;
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    public void bindAllViews(View v){
        builderCompanyEditText = v.findViewById(R.id.builderCompanyEditText);
        refNumEditText = v.findViewById(R.id.refNumEditText);
        placeEditText = v.findViewById(R.id.placeEditText);
        builtByEditText = v.findViewById(R.id.builtByEditText);
        userCompanyEditText = v.findViewById(R.id.userCompanyEditText);
        phoneEditText = v.findViewById(R.id.phoneEditText);
        contactPersonEditText = v.findViewById(R.id.contactPersonEditText);
        builderNameControlEditText = v.findViewById(R.id.builderNameControlEditText);
        userNameControlEditText = v.findViewById(R.id.userNameControlEditText);
        builderControlDateEditText = v.findViewById(R.id.builderControlDateEditText);
        userControlDateEditText = v.findViewById(R.id.userControlDateEditText);

        saveCsButton = v.findViewById(R.id.saveCsButton);
    }

    private void setOnClicks() {
        builderControlDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(v);
            }
        });
        userControlDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(v);
            }
        });

        saveCsButton.setOnClickListener(new View.OnClickListener() {
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
