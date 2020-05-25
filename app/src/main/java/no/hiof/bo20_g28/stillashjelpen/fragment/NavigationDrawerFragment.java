package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import no.hiof.bo20_g28.stillashjelpen.MainActivity;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.WallActivity;

public class NavigationDrawerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private TextView navn_header_company_name;
    private TextView nav_header_company_email;

    public NavigationDrawerFragment() {}


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NavigationView view = (NavigationView) inflater.inflate(R.layout.fragment_navigation_drawer, container);
        View header = view.getHeaderView(0);

        navn_header_company_name = header.findViewById(R.id.navn_header_company_name);
        nav_header_company_email = header.findViewById(R.id.nav_header_company_email);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        navn_header_company_name.setText("Stillashjelpen");
        if (firebaseAuth.getCurrentUser() != null) {
            nav_header_company_email.setText(firebaseAuth.getCurrentUser().getEmail());
        }

        navigationView = view.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        return view;
    }

    public void setupDrawer(DrawerLayout setDrawerLayout, Toolbar toolbar) {
        drawerLayout = setDrawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_logout) {
            Toast.makeText(getActivity(), "Du har blitt logget ut", Toast.LENGTH_SHORT).show();
            logOut();
        } else if (item.getItemId() == R.id.nav_home) {
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
        }
        else if (item.getItemId() == R.id.nav_calculator) {
            Intent i = new Intent(getActivity(), WallActivity.class);
            i.putExtra("isQuickCalculation", true);
            startActivity(i);
        }
        else
            Toast.makeText(getActivity(), "To be added.", Toast.LENGTH_SHORT).show();

        return true;
    }


    // TODO: Use this method to increase the size of menu in Navigation Drawer
    // currently not working

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.drawer_menu, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString sString = new SpannableString(menu.getItem(i).getTitle().toString());
            sString.setSpan(new RelativeSizeSpan(3f), 0, sString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(sString);
        }
    }

    public void logOut() {
        // sign out the current user
        FirebaseAuth.getInstance().signOut();

        // refresh
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
    }
}
