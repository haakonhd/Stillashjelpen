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
import androidx.viewpager2.widget.ViewPager2;
import no.hiof.bo20_g28.stillashjelpen.adapter.TabCalculationAdapter;

public class WallActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    public static boolean isQuickCalculation;


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

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

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

}
