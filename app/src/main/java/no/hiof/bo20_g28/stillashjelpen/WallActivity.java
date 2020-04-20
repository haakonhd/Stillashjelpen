package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import no.hiof.bo20_g28.stillashjelpen.adapter.TabCalculationAdapter;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class WallActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    private String from;


    private TabCalculationAdapter tabCalculationAdapter() {
        TabCalculationAdapter adapter = new TabCalculationAdapter(this);
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        Intent i = getIntent();
        from = i.getStringExtra("from");

        if(from.equals("fastCalculation")){
            //TODO - Make wall tab go away
        }

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(tabCalculationAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(getTabTextFromPosition(position));
                        setTabIcon(position);
                    }
                }).attach();
    }


    private String getTabTextFromPosition(int position) {
        if (position == 0) return "Vegginformasjon";
        if (position == 1) return "Areal for underlagsplank";
        if (position == 2) return "Forankringsavstand";
            //TODO: handle error
        else return "error";
    }

    private void setTabIcon(int position) {
        int[] tabIcons = {
                R.drawable.ic_wall_info,
                R.drawable.ic_wall_info,
                R.drawable.ic_wall_info
        };
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            tab.setIcon(tabIcons[position]);
        }
    }
}
