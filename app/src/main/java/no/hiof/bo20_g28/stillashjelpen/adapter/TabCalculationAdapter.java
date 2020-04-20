package no.hiof.bo20_g28.stillashjelpen.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import no.hiof.bo20_g28.stillashjelpen.WallActivity;
import no.hiof.bo20_g28.stillashjelpen.fragment.SoleBoardAreaFragment;
import no.hiof.bo20_g28.stillashjelpen.fragment.WallAnchorDistanceFragment;
import no.hiof.bo20_g28.stillashjelpen.fragment.WallInfoFragment;

public class TabCalculationAdapter extends FragmentStateAdapter {

    private boolean isQuickCalculation;

    public TabCalculationAdapter(Fragment fragment) {
        super(fragment);
    }

    public TabCalculationAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        // if quick calculation, the first if-statement will be skipped
        if(WallActivity.isQuickCalculation) position += 1;

        if(position == 0) return new WallInfoFragment();
        else if(position == 1) return new SoleBoardAreaFragment();
        else if(position == 2) return new WallAnchorDistanceFragment();
        else return new WallInfoFragment();
    }

    @Override
    public int getItemCount() {
        if(WallActivity.isQuickCalculation) return 2;
        return 3;
    }


}
