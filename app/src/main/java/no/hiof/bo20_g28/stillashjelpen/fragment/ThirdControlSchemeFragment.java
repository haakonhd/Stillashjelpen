package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.adapter.ChecklistRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ChecklistItem;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefect;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import static no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity.getChecklistItemsFromPreset;

public class ThirdControlSchemeFragment extends Fragment implements ChecklistRecyclerViewAdapter.ItemClickListener, ChecklistRecyclerViewAdapter.ButtonClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public static final String ARG_OBJECT = "object";
    private RecyclerView checklistRecyclerView;
    private View view;
    private ArrayList<ChecklistItem> itemData  = new ArrayList<>();
    private ChecklistRecyclerViewAdapter checklistRecyclerViewAdapter;
    private Project thisProject;
    private View reportCsDefectDialogView;
    private Button saveCheckListButton;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_third, container, false);
        reportCsDefectDialogView = inflater.inflate(R.layout.report_cs_defect_dialog, container);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        checklistRecyclerView = view.findViewById(R.id.checklistRecyclerView);
        checklistRecyclerView.addItemDecoration(new DividerItemDecoration(checklistRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        saveCheckListButton = view.findViewById(R.id.saveCheckListButton);
        saveCheckListButton.setOnClickListener(v -> { saveCheckListToDb(); });
        checklistRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return preventHorizontalScrollOnListClick(v, event);
            }
        });

        reportCsDefectDialogView = inflater.inflate(R.layout.report_cs_defect_dialog, container, false);

        ArrayList<ChecklistItem> itemData1 = thisProject.getControlScheme().getChecklistItems();
        itemData = getChecklistItemsFromPreset();
//        if(thisProject.getControlScheme().getChecklistItems() == null)
//        else{
//        }

        fillCheckListRecyclerList();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }

    private boolean preventHorizontalScrollOnListClick(View v, MotionEvent event){
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
    private void saveCheckListToDb(){
        thisProject.getControlScheme().setChecklistItems(itemData);
        DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("projects");
        DatabaseReference projectRef = fDatabase.child(thisProject.getProjectId());
        projectRef.setValue(thisProject);
    }

    private void fillCheckListRecyclerList(){

        checklistRecyclerView = view.findViewById(R.id.checklistRecyclerView);
        checklistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        checklistRecyclerViewAdapter = new ChecklistRecyclerViewAdapter(getActivity(),itemData);
        checklistRecyclerViewAdapter.setClickListener(this);
        checklistRecyclerViewAdapter.setButtonClickListener(this);
        checklistRecyclerView.setAdapter(checklistRecyclerViewAdapter);
    }



    @Override
    public void onChecklistItemClicked(View view, int position) {
        ChecklistItem clickedItem = checklistRecyclerViewAdapter.getItem(position);

        if(clickedItem.isParent()){
            if(((CheckBox) view).isChecked()){
                for(ChecklistItem item : itemData){
                    if(item.getParentId() == position){
                        if(!item.isChecked()) {
                            checklistRecyclerView.findViewHolderForLayoutPosition(item.getId()).itemView.performClick();
                            view.setEnabled(false);
                        }
                    }
                }
             }
        }
        else{
            //is child
            ChecklistItem parent = itemData.get(clickedItem.getParentId());
            if(!clickedItem.isChecked()) {
                parent.decrementCheckedChildrenCounter();
                if(parent.isChecked()){
                    checklistRecyclerView.findViewHolderForLayoutPosition(parent.getId()).itemView.findViewById(R.id.cl_checkbox).setEnabled(true);
                    checklistRecyclerView.findViewHolderForLayoutPosition(parent.getId()).itemView.performClick();
                }
            }else{
                parent.incrementCheckedChildrenCounter();
                if(!parent.isChecked()){
                    if(parent.allChildrenAreChecked()){
                        checklistRecyclerView.findViewHolderForLayoutPosition(parent.getId()).itemView.performClick();
                        checklistRecyclerView.findViewHolderForLayoutPosition(parent.getId()).itemView.findViewById(R.id.cl_checkbox).setEnabled(false);
                    }
                }
            }
        }
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }


    @Override
    public void onButtonClicked(String checkItemText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getLayoutInflater().inflate(R.layout.report_cs_defect_dialog, null);
        builder.setTitle("Rapporter mangel");
        EditText editText = v.findViewById(R.id.defectDescriptionEditText);
        editText.setText(checkItemText + ": ");

        builder.setView(v);

        Button clearButton = v.findViewById(R.id.clearDescriptionButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        builder.setPositiveButton("Rapporter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date date = Calendar.getInstance().getTime();
                String text = editText.getText().toString();
                if(!text.equals("")){
                    ControlSchemeActivity.addControlSchemeDefect(new ControlSchemeDefect(date, text));

                    DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("projects");
                    DatabaseReference projectRef = fDatabase.child(thisProject.getProjectId());
                    projectRef.setValue(thisProject);
                }
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
