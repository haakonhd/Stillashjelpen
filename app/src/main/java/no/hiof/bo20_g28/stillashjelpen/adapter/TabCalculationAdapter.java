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
        Fragment fragment;
        if(position == 0) fragment = new WallInfoFragment();
        else if(position == 1) fragment = new SoleBoardAreaFragment();
        else if(position == 2) fragment = new WallAnchorDistanceFragment();
        else  fragment = new WallAnchorDistanceFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(SoleBoardAreaFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
