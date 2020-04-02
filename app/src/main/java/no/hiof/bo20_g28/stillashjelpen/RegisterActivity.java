package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;
    private Button registerButton;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        registerButton = findViewById(R.id.registerButton);
    }

    public void registerButtonClicked(View view) {
        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        progressDialog.setMessage("Registrering pågår...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            Toast.makeText(getApplicationContext(),"Registrering fullført", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

   /* @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }*/
}
