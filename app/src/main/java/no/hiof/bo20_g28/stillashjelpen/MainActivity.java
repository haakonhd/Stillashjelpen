package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.bo20_g28.stillashjelpen.adapter.ProjectRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProjectRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseProjects;

    private ArrayList<Project> projects = new ArrayList<>();

    private ProjectRecyclerViewAdapter projectRecyclerViewAdapter;

    private RecyclerView mainRecyclerView;
    ProgressDialog progressDialog;
    private TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testText = findViewById(R.id.testText);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseProjects = FirebaseDatabase.getInstance().getReference("projects");

        if (firebaseAuth.getCurrentUser() == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        else{
            testText.setText("Logget inn på email: \n" + firebaseAuth.getCurrentUser().getEmail());
        }

    }

    private void addNewProjectToDatabase(String name) {
        progressDialog.setMessage("Lagrer nytt prosjekt...");
        progressDialog.show();

        Project p = new Project();
        ControlScheme cs = new ControlScheme();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference projectRef = databaseReference.child("projects").push();

        cs.setControlSchemeId(projectRef.getKey() + "-cs");
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

    private void fillRecyclerList() {
        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectRecyclerViewAdapter = new ProjectRecyclerViewAdapter(this, projects);
        projectRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(projectRecyclerViewAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Du trykket " + projectRecyclerViewAdapter.getItem(position) + " på rad nummer " + position, Toast.LENGTH_SHORT).show();
    }


    public void openNewProjectDialogbox(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nytt prosjekt");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNewProjectToDatabase(input.getText().toString());
            }
        });
        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    //------------------------Button Click Handling-------------------------------------------------


    public void logOutButtonClicked(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public void newProjectButtonClicked(View view) {
        openNewProjectDialogbox();
    }

    public void allProjectsButtonClicked(View view) {
        Toast.makeText(this, "Du trykket på 'Alle Prosjekter'-knappen", Toast.LENGTH_SHORT).show();

    }

    public void fastCalcButtonClicked(View view) {
        Toast.makeText(this, "Du trykket på 'Hurtig-utregning'-knappen", Toast.LENGTH_SHORT).show();
    }
}
