package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.adapter.ChecklistRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ChecklistItem;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import static no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity.getChecklistItems;

public class ThirdControlSchemeFragment extends Fragment implements ChecklistRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public static final String ARG_OBJECT = "object";
    private RecyclerView checklistRecyclerView;
    private View view;
    private ArrayList<ChecklistItem> itemData  = new ArrayList<>();
    private ChecklistRecyclerViewAdapter checklistRecyclerViewAdapter;
    private Project thisProject;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_third, container, false);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        checklistRecyclerView = view.findViewById(R.id.checklistRecyclerView);
        checklistRecyclerView.addItemDecoration(new DividerItemDecoration(checklistRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        itemData = getChecklistItems();
        fillCheckListRecyclerList();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }

    private void preventHorizontalScrollOnListClick(){

    }

    private void fillCheckListRecyclerList(){

        checklistRecyclerView = view.findViewById(R.id.checklistRecyclerView);
        checklistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        checklistRecyclerViewAdapter = new ChecklistRecyclerViewAdapter(getActivity(),itemData);
        checklistRecyclerViewAdapter.setClickListener(this);
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
                        }
                    }
                }
             }
        }
        else{
            ChecklistItem parent = itemData.get(clickedItem.getParentId());
            if(!clickedItem.isChecked()) {
                parent.decrementCheckedChildrenCounter();
                if(parent.isChecked()){
                    checklistRecyclerView.findViewHolderForLayoutPosition(parent.getId()).itemView.performClick();
                }
            }else{
                parent.incrementCheckedChildrenCounter();
                if(!parent.isChecked()){
                    if(parent.allChildrenAreChecked())
                        checklistRecyclerView.findViewHolderForLayoutPosition(parent.getId()).itemView.performClick();
                }
            }
        }
    }

    @Override
    public void onMethodCallback() {
        Log.e("NICE", "NIIIIICE");
    }
}
