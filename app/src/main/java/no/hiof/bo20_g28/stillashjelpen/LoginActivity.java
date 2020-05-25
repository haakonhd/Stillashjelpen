package no.hiof.bo20_g28.stillashjelpen;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;
    private TextView registerClickableTextView;
    private Button loginButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        registerClickableTextView = findViewById(R.id.registerClickableTextView);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput(emailTextView, passwordTextView))
                    loginButtonClicked();
            }
        });

        registerClickableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

    }

    public boolean validateInput(TextView emailTextView, TextView passwordTextView) {

        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        if(email.equals("") || password.equals("")) {
            Toast.makeText(this, "Du må fylle ut både epost og passord", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void loginButtonClicked() {
        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        progressDialog.setMessage("Logger inn...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            Toast.makeText(getApplicationContext(),"Innlogging fullført", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Innloggingen feilet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

