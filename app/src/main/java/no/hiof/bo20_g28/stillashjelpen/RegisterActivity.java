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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if(checkIfInputExists(email, password)){
            if(verifyEmail(email)){
                if(verifyPassword(password)){
                    registerUser(email, password);
                }
            }
        }

    }

    public boolean checkIfInputExists(String email, String password){
        if(email.equals("") || password.equals("")) {
            Toast.makeText(this, "Fyll ut både email- og passord-felt", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean verifyEmail(String email){
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches()){
            Toast.makeText(this, "Email har ikke et korrekt format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean verifyPassword(String password){
        if(password.length() < 6){
            Toast.makeText(this, "Passord må bestå av minimum 6 tegn", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void registerUser(String email, String password){
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
                            Toast.makeText(getApplicationContext(),"Registreringen er fullført", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registreringen feilet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
