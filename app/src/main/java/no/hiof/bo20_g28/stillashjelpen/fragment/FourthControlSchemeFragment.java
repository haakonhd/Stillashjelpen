package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.adapter.DefectFixedRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefectFixed;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import static no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity.getControlSchemeDefectFixed;

public class FourthControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    TextView textView;
    private View view;
    private RecyclerView controlSchemeDefectFixedRecyclerView;
    private DefectFixedRecyclerViewAdapter defectFixedRecyclerViewAdapter;
    private Project thisProject;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_fourth, container, false);
        Log.d("FOURTH", "Entered 4th control");
        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        controlSchemeDefectFixedRecyclerView = view.findViewById(R.id.defectFixedRecyclerView);
        fillRecyclerList(getControlSchemeDefectFixed());

        return view;
    }


    public void fillRecyclerList(ArrayList<ControlSchemeDefectFixed> data) {
        controlSchemeDefectFixedRecyclerView = view.findViewById(R.id.defectFixedRecyclerView);
        controlSchemeDefectFixedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        defectFixedRecyclerViewAdapter = new DefectFixedRecyclerViewAdapter(getActivity(), data);
        controlSchemeDefectFixedRecyclerView.setAdapter(defectFixedRecyclerViewAdapter);
    }
}
