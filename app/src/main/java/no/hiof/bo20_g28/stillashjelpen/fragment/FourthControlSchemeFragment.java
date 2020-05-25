package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.adapter.DefectFixedRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.adapter.DefectRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefect;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefectFixed;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import static no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity.getControlSchemeDefectFixed;
import static no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity.getControlSchemeItems;

public class FourthControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    TextView textView;
    private View view;
    private RecyclerView controlSchemeDefectFixedRecyclerView;
    private RecyclerView controlSchemeDefectRecyclerView;
    private DefectFixedRecyclerViewAdapter defectFixedRecyclerViewAdapter;
    private DefectRecyclerViewAdapter defectRecyclerViewAdapter;
    private Project thisProject;
    private ArrayList<ControlSchemeDefect> schemeDefectData = new ArrayList<>();
    private ArrayList<ControlSchemeDefectFixed> schemeDefectFixedData = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        fillItemsFromDb();
    }

    private void fillItemsFromDb() {
        schemeDefectData = thisProject.getControlScheme().getControlSchemeDefects();
        schemeDefectFixedData = thisProject.getControlScheme().getControlSchemeDefectFixed();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_fourth, container, false);
        Log.d("FOURTH", "Entered 4th control");
        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        assert thisProject != null;
        fillItemsFromDb();

        controlSchemeDefectFixedRecyclerView = view.findViewById(R.id.defectFixedRecyclerView);
        fillRecyclerList();
        fillDefectRecyclerList();
        Button newDefectButton = view.findViewById(R.id.newDefectButton);
        Button newDefectFixedButton = view.findViewById(R.id.newDefectFixedButton);

        newDefectButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View viewInflate = getLayoutInflater().inflate(R.layout.report_cs_defect_dialog, null);
            builder.setTitle("Register ny Mangel");

            EditText editText = viewInflate.findViewById(R.id.defectDescriptionEditText);
            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(28)});
            builder.setView(viewInflate);

            builder.setPositiveButton("Rapporter", (dialog, which) -> {
               Date date = Calendar.getInstance().getTime();
               String text = editText.getText().toString();

               if(!text.equals("")) {
                   thisProject.getControlScheme().addControlSchemeDefect(new ControlSchemeDefect(date, text));

                   DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("projects");
                   DatabaseReference projectRef = fDatabase.child(thisProject.getProjectId());
                   projectRef.setValue(thisProject);
               }
            });

            builder.setNegativeButton("Avbryt", (dialog, which) -> {
                dialog.cancel();
            });


            builder.show();
        });

        newDefectFixedButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View viewInflate = getLayoutInflater().inflate(R.layout.report_cs_defect_dialog, null);
            builder.setTitle("Register ny Mangel Utbedret");

            EditText editText = viewInflate.findViewById(R.id.defectDescriptionEditText);
            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});

            builder.setView(viewInflate);

            builder.setPositiveButton("Rapporter", (dialog, which) -> {
                Date dateFound = Calendar.getInstance().getTime();
                Date dateFixed = Calendar.getInstance().getTime();

                String text = editText.getText().toString();


                if(!text.equals("")) {
                    thisProject.getControlScheme().addControlSchemeDefectFixed(new ControlSchemeDefectFixed(dateFound, dateFixed, text));

                    DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("projects");
                    DatabaseReference projectRef = fDatabase.child(thisProject.getProjectId());
                    projectRef.setValue(thisProject);
                }
            });

            builder.setNegativeButton("Avbryt", (dialog, which) -> {
                dialog.cancel();
            });


            builder.show();
        });

        return view;
    }


    private void fillRecyclerList() {
        controlSchemeDefectFixedRecyclerView = view.findViewById(R.id.defectFixedRecyclerView);
        controlSchemeDefectFixedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        defectFixedRecyclerViewAdapter = new DefectFixedRecyclerViewAdapter(getActivity(), schemeDefectFixedData);
        controlSchemeDefectFixedRecyclerView.setAdapter(defectFixedRecyclerViewAdapter);
    }

    private void fillDefectRecyclerList() {
        controlSchemeDefectRecyclerView = view.findViewById(R.id.defectRecyclerView);
        controlSchemeDefectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        defectRecyclerViewAdapter = new DefectRecyclerViewAdapter(getActivity(), schemeDefectData);
        controlSchemeDefectRecyclerView.setAdapter(defectRecyclerViewAdapter);
    }

}
