package no.hiof.bo20_g28.stillashjelpen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import no.hiof.bo20_g28.stillashjelpen.adapter.TabControlSchemeAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

public class ControlSchemeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Project thisProject;

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
}
