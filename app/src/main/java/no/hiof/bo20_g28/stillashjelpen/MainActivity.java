package no.hiof.bo20_g28.stillashjelpen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!loggedIn) {
            Intent navigateToLoginActivityIntent = new Intent(this, LoginActivity.class);
            Toast.makeText(getApplicationContext(), "Du er ikke innlogget.", Toast.LENGTH_SHORT).show();
            startActivity(navigateToLoginActivityIntent);
        }
    }
}
