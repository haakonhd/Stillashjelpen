package no.hiof.bo20_g28.stillashjelpen;

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

        Intent intent = getIntent();
        thisProject = (Project) intent.getSerializableExtra("passedProject");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Kontrollskjema for prosjekt: " + thisProject.getProjectName());


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
        checklistItems.add(new ChecklistItem(true, id++, "Skilting av stillas", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Bærende konstruksjon", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Atkomst", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Stillasgulv", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Rekkverk", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Håndlist", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Knelist", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Fotlist", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Skvett/skjerm", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Presenning/Nett", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Fundamentering", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Avstivning", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Forankring", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Feste for forankring", 0));
        return checklistItems;
    }




    public static ArrayList<ControlSchemeDefectFixed> getControlSchemeDefectFixed() {
        controlSchemeDefectFixedItems.clear();
        controlSchemeDefectFixedItems.add(new ControlSchemeDefectFixed(new Date(), new Date(), "Bjarne"));
        controlSchemeDefectFixedItems.add(new ControlSchemeDefectFixed(new Date(), new Date(), "Leif"));
        controlSchemeDefectFixedItems.add(new ControlSchemeDefectFixed(new Date(), new Date(), "Knut"));
        controlSchemeDefectFixedItems.add(new ControlSchemeDefectFixed(new Date(), new Date(), "Brynjar"));
        return controlSchemeDefectFixedItems;
    }

    public static ArrayList<ControlSchemeDefect> getControlSchemeItems() {
        controlSchemeDefectItems.clear();
        controlSchemeDefectItems.add(new ControlSchemeDefect(new Date(), "Skilting av stillas"));
        controlSchemeDefectItems.add(new ControlSchemeDefect(new Date(), "Stillasgulv"));
        controlSchemeDefectItems.add(new ControlSchemeDefect(new Date(), "Forankring"));

        return controlSchemeDefectItems;
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
        DatabaseReference fDatabaseRoot = database.getReference().child("projects");

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
                Log.w("FirebaseError", databaseError.toException());
            }
        });
    }
}
