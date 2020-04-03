package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.bo20_g28.stillashjelpen.adapter.ProjectRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProjectRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseProjects;

    private ArrayList<Project> projects = new ArrayList<>();

    private ProjectRecyclerViewAdapter projectRecyclerViewAdapter;

    private RecyclerView mainRecyclerView;
    private TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseProjects = FirebaseDatabase.getInstance().getReference("projects");

        testText = findViewById(R.id.testText);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);

        if (firebaseAuth.getCurrentUser() == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        else{
            testText.setText("Logget inn på email: \n" + firebaseAuth.getCurrentUser().getEmail());
            testFirebaseDatabase();
        }

    }

    private void testFirebaseDatabase() {
        Project p = new Project();
        ControlScheme cs = new ControlScheme();

        DatabaseReference projectRef = databaseReference.child("projects").push();

        cs.setControlSchemeId(projectRef.getKey() + "-cs");
        p.setProjectId(projectRef.getKey());
        p.setUserId(firebaseAuth.getCurrentUser().getUid());
        p.setProjectName("Møkkaløkka 21");
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
                getProjectsFromDatabase(firebaseAuth.getCurrentUser().getUid(), dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void logOutButtonClicked(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
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
}
