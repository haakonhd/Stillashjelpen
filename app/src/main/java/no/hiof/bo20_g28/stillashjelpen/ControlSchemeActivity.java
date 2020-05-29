package no.hiof.bo20_g28.stillashjelpen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import no.hiof.bo20_g28.stillashjelpen.adapter.TabControlSchemeAdapter;
import no.hiof.bo20_g28.stillashjelpen.fragment.NavigationDrawerFragment;
import no.hiof.bo20_g28.stillashjelpen.model.ChecklistItem;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefect;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefectFixed;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

public class ControlSchemeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Project thisProject;
    private Toolbar toolbar;
    private static Context context;
    private static ArrayList<ChecklistItem> checklistItems = new ArrayList<>();
    private static ArrayList<ControlSchemeDefectFixed> controlSchemeDefectFixedItems = new ArrayList<>();
    private static ArrayList<ControlSchemeDefect> controlSchemeDefectItems = new ArrayList<>();

    private TabControlSchemeAdapter tabControlSchemeAdapter() {
        TabControlSchemeAdapter cSadapter = new TabControlSchemeAdapter(this);
        return cSadapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_scheme);
        context = this;

        Intent intent = getIntent();
        thisProject = (Project) intent.getSerializableExtra(getString(R.string.passedProject));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.kontrollSkjemaForProsjekt) + thisProject.getProjectName());


        setUpNavigationDrawer();

        viewPager = findViewById(R.id.cs_pager);
        tabLayout = findViewById(R.id.tab_cs_layout);

        viewPager.setAdapter(tabControlSchemeAdapter());
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
            }
        }).attach();

        setTabLayout();
        setTabText();
    }

    private void setTabText(){
        tabLayout.getTabAt(0).setText(getString(R.string.tab_cs_first));
        tabLayout.getTabAt(1).setText(getString(R.string.tab_cs_second));
        tabLayout.getTabAt(2).setText(getString(R.string.tab_cs_third));
        tabLayout.getTabAt(3).setText(getString(R.string.tab_cs_fourth));
        tabLayout.getTabAt(4).setText(getString(R.string.pdf));
    }

    private int[] tabLabels = {
            R.string.tab_cs_first,
            R.string.tab_cs_second,
            R.string.tab_cs_third,
            R.string.tab_cs_fourth,
            R.string.pdf
    };

    private void setTabLayout(){
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_cs_tab, null);

            TextView tab_label = (TextView) tab.findViewById(R.id.tab_cs_label);

            tab_label.setText(tabLabels[i]);
        }
    }

    public static ArrayList<ChecklistItem> getChecklistItemsFromPreset(){
        int id = 0;
        checklistItems.clear();
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_skilting), 0));
        checklistItems.add(new ChecklistItem(true, id++,  context.getString(R.string.cl_baerende), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_atkomst), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_stillasgulv), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_rekkverk), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_handlist), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_knelist), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_fotlist), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_skvett), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_prenning_nett), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_fundamentering), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_avstivning), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_forankring), 0));
        checklistItems.add(new ChecklistItem(true, id++, context.getString(R.string.cl_feste), 0));
        return checklistItems;
    }

    private void setUpNavigationDrawer() {
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        if(navigationDrawerFragment != null) {
            navigationDrawerFragment.setupDrawer(drawerlayout, toolbar);
        }
    }

    public void newDefectFixedButtonClicked(View view) {

    }

    public void newDefectButtonClicked(View view) {
    }

    public void updateProjectFromFirebase(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference().child(getString(R.string.projects));

        fDatabaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    Project project = addressSnapshot.child(thisProject.getProjectId()).getValue(Project.class);
                    if (project != null) {
                        thisProject = project;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(getString(R.string.firebaseError), databaseError.toException());
            }
        });
    }
}
