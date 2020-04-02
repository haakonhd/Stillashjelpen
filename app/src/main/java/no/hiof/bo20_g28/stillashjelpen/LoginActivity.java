package no.hiof.bo20_g28.stillashjelpen;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class LoginActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Brukeren er logget inn.", Toast.LENGTH_SHORT).show();

                // TODO: Login handling (firebase)
                // Intent navigateToMainActivityIntent = new Intent(this, MainActivity.class);
                // startActivity(navigateToMainActivityIntent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Navigerer til registrering. ", Toast.LENGTH_SHORT).show();

                // TODO: Add a Register Activity
                // Intent navigateToRegisterActivityIntent = new Intent(this, RegisterActivity.class);
                // startActivity(navigateToRegisterActivityIntent);
            }
        });

    }
}
