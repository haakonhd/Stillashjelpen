package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.bo20_g28.stillashjelpen.adapter.ProjectRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.fragment.NavigationDrawerFragment;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProjectRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private DatabaseReference databaseProjects;
    private ArrayList<Project> projects = new ArrayList<>();
    private ProjectRecyclerViewAdapter projectRecyclerViewAdapter;
    private RecyclerView mainRecyclerView;
    private TextView testText;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private final List<String> scaffoldingSystemList = new ArrayList<String>();


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
        //uploadScaffoldingSystemTest();
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

    private void getProjectsFromDatabase(String userId, DataSnapshot dataSnapshot){
        projects.clear();

        for(DataSnapshot projectSnapshot: dataSnapshot.getChildren()){
            Project project = projectSnapshot.getValue(Project.class);
            if(project.getUserId().equals(userId)) {
                projects.add(project);
            }
        }

        fillRecyclerList();
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


    private void fillRecyclerList() {
        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectRecyclerViewAdapter = new ProjectRecyclerViewAdapter(this, projects);
        projectRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(projectRecyclerViewAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Project clickedItem = projectRecyclerViewAdapter.getItem(position);
        Intent i = new Intent(this, ProjectActivity.class);
        i.putExtra("passedProject", clickedItem);
        startActivity(i);
    }

    public void uploadScaffoldingSystemsFromFirebase(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference().child("scaffoldingSystems");

        scaffoldingSystemList.clear();

        fDatabaseRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                scaffoldingSystemList.add("Velg et stillassystem");

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
        uploadScaffoldingSystemsFromFirebase();
    }

    public void allProjectsButtonClicked(View view) {
        Toast.makeText(this, "Du trykket på 'Alle Prosjekter'-knappen", Toast.LENGTH_SHORT).show();
    }

    public void fastCalcButtonClicked(View view) {
        Intent i = new Intent(this, WallActivity.class);
        i.putExtra("isQuickCalculation", true);
        startActivity(i);
    }



    private void uploadScaffoldingSystemTest(){
        ScaffoldingSystem ss = new ScaffoldingSystem();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scaffoldingSystemsRef = databaseRef.child("scaffoldingSystems").push();

        ss.setScaffoldingSystemId(scaffoldingSystemsRef.getKey());
        ss.setScaffoldingSystemName("none");
        ss.setBayLength(0);
        ss.setBayWidth(0);
        ss.setWeight(0);
        ss.setScaffoldLoadClass(1);

        scaffoldingSystemsRef.setValue(ss);
    }

    /*private void spinner(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        fDatabaseRoot.child("scaffoldingSystems").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> projectsList = new ArrayList<String>();

                for (DataSnapshot addressSnapshot: dataSnapshot.getChildren()) {
                    String project = addressSnapshot.child("scaffoldingSystemName").getValue(String.class);
                    if (project!=null){
                        projectsList.add(project);
                    }
                }

                Spinner spinnerProperty = (Spinner) findViewById(R.id.testSpinner);
                ArrayAdapter<String> scaffoldSystemAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, projectsList);
                scaffoldSystemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProperty.setAdapter(scaffoldSystemAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
}
