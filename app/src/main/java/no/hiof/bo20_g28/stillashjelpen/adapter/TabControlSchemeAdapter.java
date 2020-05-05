package no.hiof.bo20_g28.stillashjelpen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import no.hiof.bo20_g28.stillashjelpen.fragment.FirstControlSchemeFragment;
import no.hiof.bo20_g28.stillashjelpen.fragment.FourthControlSchemeFragment;
import no.hiof.bo20_g28.stillashjelpen.fragment.SecondControlSchemeFragment;
import no.hiof.bo20_g28.stillashjelpen.fragment.ThirdControlSchemeFragment;

public class TabControlSchemeAdapter extends FragmentStateAdapter {

    public TabControlSchemeAdapter(Fragment fragment) {
        super(fragment);
    }

    public TabControlSchemeAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)

        if(position == 0) return new FirstControlSchemeFragment();
        else if(position == 1) return new SecondControlSchemeFragment();
        else if(position == 2) return new ThirdControlSchemeFragment();
        else if(position == 3) return new FourthControlSchemeFragment();
        else return new FirstControlSchemeFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
