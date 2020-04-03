package no.hiof.bo20_g28.stillashjelpen;

import androidx.appcompat.app.AppCompatActivity;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        testText = findViewById(R.id.testText);

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

        DatabaseReference projectRef = databaseReference.child("projects").push();

        p.setProjectId(projectRef.getKey());
        p.setUserId(firebaseAuth.getCurrentUser().getUid());
        p.setProjectName("Møkkaløkka 21");

        projectRef.setValue(p);
    }

    public void logOutButtonClicked(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }
}
