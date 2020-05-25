package no.hiof.bo20_g28.stillashjelpen;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
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
import no.hiof.bo20_g28.stillashjelpen.adapter.TabCalculationAdapter;
import no.hiof.bo20_g28.stillashjelpen.fragment.NavigationDrawerFragment;
import no.hiof.bo20_g28.stillashjelpen.model.Project;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

public class WallActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Toolbar toolbar;
    private Project thisProject;
    private Wall thisWall;
    public static boolean isQuickCalculation;
    public static double wallAnchorDistance;
    public static int soleBoardArea;


    private TabCalculationAdapter tabCalculationAdapter() {
        TabCalculationAdapter adapter = new TabCalculationAdapter(this);
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        isQuickCalculation = Objects.requireNonNull(intent.getExtras()).getBoolean("isQuickCalculation");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        Intent i = getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");
        thisWall = (Wall) i.getSerializableExtra("passedWall");
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            Objects.requireNonNull(getSupportActionBar()).setElevation(0);
            Objects.requireNonNull(getSupportActionBar()).setTitle(thisProject.getProjectName() + " > " + thisWall.getWallName());
        }
        catch(Exception e) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Vegg-side");
        }

        setUpNavigationDrawer();

        viewPager.setAdapter(tabCalculationAdapter());
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    }
                }).attach();

        setTabLayout();
    }

    private int[] tabIcons = {
        R.drawable.ic_wall_info_light,
        R.drawable.ic_soleboard,
        R.drawable.ic_anchor
    };

    private int[] tabLabels = {
            R.string.tab_wall_info,
            R.string.tab_soleboard,
            R.string.tab_anchor
    };

    private void setTabLayout(){
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

            TextView tab_label = (TextView) tab.findViewById(R.id.tab_label);
            ImageView tab_icon = (ImageView) tab.findViewById(R.id.tab_icon);

            if(isQuickCalculation) tab_label.setText(tabLabels[i+1]);
            else tab_label.setText(tabLabels[i]);

            if(isQuickCalculation) tab_icon.setImageResource(tabIcons[i+1]);
            else tab_icon.setImageResource(tabIcons[i]);
            tabLayout.getTabAt(i).setCustomView(tab);
        }
    }

    private void setUpNavigationDrawer() {
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        if(navigationDrawerFragment != null) {
            navigationDrawerFragment.setupDrawer(drawerlayout, toolbar);
        }
    }

}
