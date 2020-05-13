package no.hiof.bo20_g28.stillashjelpen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.bo20_g28.stillashjelpen.adapter.ProjectRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.adapter.ScaffoldSystemRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.fragment.NavigationDrawerFragment;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.Project;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;

public class MainActivity extends AppCompatActivity implements ProjectRecyclerViewAdapter.ItemClickListener, ScaffoldSystemRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private DatabaseReference databaseProjects;
    private ArrayList<Project> projects = new ArrayList<>();
    private ArrayList<ScaffoldingSystem> scaffoldSystems = new ArrayList<>();
    private ProjectRecyclerViewAdapter projectRecyclerViewAdapter;
    private ScaffoldSystemRecyclerViewAdapter scaffoldSystemRecyclerViewAdapter;
    private RecyclerView mainRecyclerView;
    private TextView testText;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private final List<String> scaffoldingSystemList = new ArrayList<String>();

    private Button showProjectsButton, showScaffoldSystemsButton;

    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        setUpNavigationDrawer();

        testText = findViewById(R.id.testText);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        showProjectsButton = findViewById(R.id.showProjectsButton);
        showScaffoldSystemsButton = findViewById(R.id.showScaffoldSystemsButton);

        showProjectsButton.setBackgroundResource(R.drawable.border_filled);
        showProjectsButton.setTextColor(getResources().getColor(R.color.white));
        showScaffoldSystemsButton.setBackgroundResource(R.drawable.border);
        showScaffoldSystemsButton.setTextColor(getResources().getColor(R.color.colorAccent));

        // if statement fixes a crash that occurs when app opens MainActivity the second time from new Intent
        if (!calledAlready) {
            // offline/online sync
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        databaseProjects = FirebaseDatabase.getInstance().getReference("projects");

        //check if user is signed in
        if (firebaseAuth.getCurrentUser() == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        else{
            testText.setText(Html.fromHtml("Logget inn på email: <font color='#01C6DB'>" + firebaseAuth.getCurrentUser().getEmail() + "</font>"));
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    private void addNewProjectToDatabase(String name, String scaffoldingSystem) {
        Project p = new Project();
        ControlScheme cs = new ControlScheme();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference projectRef = databaseReference.child("projects").push();

        cs.setControlSchemeId(projectRef.getKey() + "-cs");
        p.setScaffoldType(scaffoldingSystem);
        p.setProjectId(projectRef.getKey());
        p.setUserId(firebaseAuth.getCurrentUser().getUid());
        p.setProjectName(name);
        p.setControlScheme(cs);

        projectRef.setValue(p);
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseProjects.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    getProjectsFromDatabase(firebaseAuth.getCurrentUser().getUid(), dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpNavigationDrawer() {
        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentNavigationDrawer);
        if(navigationDrawerFragment != null) {
            navigationDrawerFragment.setupDrawer(drawerlayout, toolbar);
        }
    }

    private void fillRecyclerListProjects() {
        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectRecyclerViewAdapter = new ProjectRecyclerViewAdapter(this, projects);
        projectRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(projectRecyclerViewAdapter);
    }

    private void fillRecyclerListScaffoldSystems() {
        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scaffoldSystemRecyclerViewAdapter = new ScaffoldSystemRecyclerViewAdapter(this, scaffoldSystems);
        scaffoldSystemRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(scaffoldSystemRecyclerViewAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Project clickedItem = projectRecyclerViewAdapter.getItem(position);
        Intent i = new Intent(this, ProjectActivity.class);
        i.putExtra("passedProject", clickedItem);
        i.putExtra("from", "main");
        startActivity(i);
    }

    @Override
    public void onItemClick2(View view, int position) {
        ScaffoldingSystem clickedItem = scaffoldSystemRecyclerViewAdapter.getItem(position);
        Intent i = new Intent(this, NewScaffoldingSystemActivity.class);
        i.putExtra("passedScaffoldSystem", clickedItem);
        i.putExtra("from", "old");
        startActivity(i);
    }


    //------------------------Firebase Download Handling--------------------------------------------

    private void getProjectsFromDatabase(String userId, DataSnapshot dataSnapshot){
        projects.clear();

        for(DataSnapshot projectSnapshot: dataSnapshot.getChildren()){
            Project project = projectSnapshot.getValue(Project.class);
            if(project.getUserId().equals(userId)) {
                projects.add(project);
            }
        }

        fillRecyclerListProjects();
    }

    public void getScaffoldingSystemNamesFromFirebase(){

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
                openNewProjectCustomDialogbox();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FirebaseError", databaseError.toException());
            }
        });
    }

    public void getScaffoldSystemObjectsFromFirebase(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference().child("scaffoldingSystems");

        scaffoldSystems.clear();

        fDatabaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot scaffoldSnapshot : dataSnapshot.getChildren()) {
                    ScaffoldingSystem ss = scaffoldSnapshot.getValue(ScaffoldingSystem.class);
                    scaffoldSystems.add(ss);
                }
                fillRecyclerListScaffoldSystems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FirebaseError", databaseError.toException());
            }
        });
    }


    //------------------------Alert Dialog Handling-------------------------------------------------

    private void openNewProjectCustomDialogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.new_project_dialog_box, null);
        builder.setTitle("Nytt prosjekt");

        final Spinner spinner = (Spinner) view.findViewById(R.id.scaffoldingSystemsSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, scaffoldingSystemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set up the input
        final EditText input = (EditText) view.findViewById(R.id.editText);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO Better sanitation and handling of input
                if(!spinner.getSelectedItem().toString().equalsIgnoreCase("Velg et stillassystem")) {
                    if(input.getText().toString().length() > 0) {
                        addNewProjectToDatabase(input.getText().toString(), spinner.getSelectedItem().toString());
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Mislykket - Gi veggen et navn", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Mislykket - Velg en type stillas", Toast.LENGTH_SHORT).show();
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


    //------------------------Button Click Handling-------------------------------------------------


    public void newProjectButtonClicked(View view) {
        getScaffoldingSystemNamesFromFirebase();
    }

    public void newScaffoldSystemButtonClicked(View view) {
        Intent i = new Intent(this, NewScaffoldingSystemActivity.class);
        i.putExtra("from", "new");
        startActivity(i);
    }

    public void fastCalcButtonClicked(View view) {
        Intent i = new Intent(this, WallActivity.class);
        i.putExtra("isQuickCalculation", true);
        startActivity(i);
    }

    public void showProjectsButtonClicked(View view) {
        fillRecyclerListProjects();
        showProjectsButton.setBackgroundResource(R.drawable.border_filled);
        showProjectsButton.setTextColor(getResources().getColor(R.color.white));
        showScaffoldSystemsButton.setBackgroundResource(R.drawable.border);
        showScaffoldSystemsButton.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    public void showScaffoldSystemsButtonClicked(View view) {
        getScaffoldSystemObjectsFromFirebase();
        showScaffoldSystemsButton.setBackgroundResource(R.drawable.border_filled);
        showScaffoldSystemsButton.setTextColor(getResources().getColor(R.color.white));
        showProjectsButton.setBackgroundResource(R.drawable.border);
        showProjectsButton.setTextColor(getResources().getColor(R.color.colorAccent));
    }
}
