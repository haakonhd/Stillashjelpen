package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

public class ThirdControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    TextView textView;
    private View view;

    private Project thisProject;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_third, container, false);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        textView = view.findViewById(R.id.cs3TestTextView);
        textView.setText(getResources().getString(R.string.tab_cs_third) + " for prosjekt: " + thisProject.getProjectName());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }
}
