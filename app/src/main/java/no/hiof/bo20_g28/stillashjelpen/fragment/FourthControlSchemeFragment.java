package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_fourth, container, false);
        Log.d("FOURTH", "Entered 4th control");
        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        controlSchemeDefectFixedRecyclerView = view.findViewById(R.id.defectFixedRecyclerView);
        fillRecyclerList(getControlSchemeDefectFixed());
        fillDefectRecyclerList(getControlSchemeItems());
        Button newDefectButton = view.findViewById(R.id.newDefectButton);

        newDefectButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Registrer Ny Mangel");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Rapporter", (dialog, which) -> {

            });
            builder.setNegativeButton("Avbryt", (dialog, which) -> {
                dialog.cancel();
            });

            builder.show();
        });




        return view;
    }


    private void fillRecyclerList(ArrayList<ControlSchemeDefectFixed> data) {
        controlSchemeDefectFixedRecyclerView = view.findViewById(R.id.defectFixedRecyclerView);
        controlSchemeDefectFixedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        defectFixedRecyclerViewAdapter = new DefectFixedRecyclerViewAdapter(getActivity(), data);
        controlSchemeDefectFixedRecyclerView.setAdapter(defectFixedRecyclerViewAdapter);
    }

    private void fillDefectRecyclerList(ArrayList<ControlSchemeDefect> data) {
        controlSchemeDefectRecyclerView = view.findViewById(R.id.defectRecyclerView);
        controlSchemeDefectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        defectRecyclerViewAdapter = new DefectRecyclerViewAdapter(getActivity(), data);
        controlSchemeDefectRecyclerView.setAdapter(defectRecyclerViewAdapter);
    }

    public void newDefectFixedButtonClicked(View view) {

    }

    public void newDefectButtonClicked(View view) {

    }
}
