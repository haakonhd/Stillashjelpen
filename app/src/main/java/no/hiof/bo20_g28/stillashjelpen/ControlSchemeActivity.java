package no.hiof.bo20_g28.stillashjelpen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import no.hiof.bo20_g28.stillashjelpen.adapter.TabControlSchemeAdapter;
import no.hiof.bo20_g28.stillashjelpen.fragment.NavigationDrawerFragment;
import no.hiof.bo20_g28.stillashjelpen.model.ChecklistItem;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefect;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

public class ControlSchemeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Project thisProject;
    Toolbar toolbar;
    private static ArrayList<ChecklistItem> checklistItems = new ArrayList<>();
    private static ArrayList<ControlSchemeDefect> controlSchemeDefects = new ArrayList<>();

    public static ArrayList<ControlSchemeDefect> getControlSchemeDefects() {
        return controlSchemeDefects;
    }

    public static void setControlSchemeDefects(ArrayList<ControlSchemeDefect> controlSchemeDefects) {
        ControlSchemeActivity.controlSchemeDefects = controlSchemeDefects;
    }

    public static void addControlSchemeDefect(ControlSchemeDefect controlSchemeDefect){
        controlSchemeDefects.add(controlSchemeDefect);
    }

    private TabControlSchemeAdapter tabControlSchemeAdapter() {
        TabControlSchemeAdapter cSadapter = new TabControlSchemeAdapter(this);
        return cSadapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        thisProject = (Project) intent.getSerializableExtra("passedProject");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_scheme);

        //Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    private int[] tabLabels = {
            R.string.tab_cs_first,
            R.string.tab_cs_second,
            R.string.tab_cs_third,
            R.string.tab_cs_fourth
    };

    private void setTabLayout(){
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_cs_tab, null);

            TextView tab_label = (TextView) tab.findViewById(R.id.tab_cs_label);

            tab_label.setText(tabLabels[i]);
        }
    }

    public static ArrayList<ChecklistItem> getChecklistItems(){
        int id = 0;
        checklistItems.clear();
        checklistItems.add(new ChecklistItem(true, id++, "Skilt?", 2));
        checklistItems.add(new ChecklistItem(id++, "Rødt skilt?", 0));
        checklistItems.add(new ChecklistItem(id++, "Blått skilt?", 0));
        checklistItems.add(new ChecklistItem(true, id++, "Gulv?", 1));
        checklistItems.add(new ChecklistItem(id++, "Brunt gulv?", 3));
        return checklistItems;
    }

    private void setUpNavigationDrawer() {
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        if(navigationDrawerFragment != null) {
            navigationDrawerFragment.setupDrawer(drawerlayout, toolbar);
        }
    }
}
