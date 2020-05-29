package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
    private final List<String> scaffoldingSystemList = new ArrayList<String>();


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
//            nav_header_company_email.setText("stillashjelpen@byggefolka.no");
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
            Toast.makeText(getActivity(), "Du har blitt logget ut", Toast.LENGTH_LONG).show();
            logOut();
        } else if (item.getItemId() == R.id.nav_home) {
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
        }
        else if (item.getItemId() == R.id.nav_calculator) {
            getFastCalcScaffoldingSystemNamesFromFirebase();
        }
        else
            Toast.makeText(getActivity(), "To be added.", Toast.LENGTH_LONG).show();

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

    public void getFastCalcScaffoldingSystemNamesFromFirebase(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference().child("scaffoldingSystems");

        scaffoldingSystemList.clear();

        fDatabaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                scaffoldingSystemList.add("Velg en type stillassystem");

                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    String project = addressSnapshot.child("scaffoldingSystemName").getValue(String.class);
                    if (project != null) {
                        scaffoldingSystemList.add(project);
                    }
                }
                openFastClacDialogbox();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FirebaseError", databaseError.toException());
            }
        });
    }

    private void openFastClacDialogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.choose_scaffoldingsystem_dialog_box, null);
        builder.setTitle("Velg stillassystem");

        final Spinner spinner = (Spinner) view.findViewById(R.id.fastCalcScaffoldingSystemsSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, scaffoldingSystemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO Better sanitation and handling of input
                if(!spinner.getSelectedItem().toString().equalsIgnoreCase("Velg en type stillassystem")) {
                    Intent i = new Intent(getContext(), WallActivity.class);
                    i.putExtra("isQuickCalculation", true);
                    i.putExtra("scaffoldingSystem", spinner.getSelectedItem().toString());
                    startActivity(i);
                }
                else{
                    Toast.makeText(getContext(), "Mislykket - Velg en type stillas", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(view);
        AlertDialog ad = builder.create();
        ad.show();
    }
}
