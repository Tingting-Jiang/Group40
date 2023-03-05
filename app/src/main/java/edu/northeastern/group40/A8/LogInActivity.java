package edu.northeastern.group40.A8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;
import edu.northeastern.group40.R;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LogIn";


    private FirebaseAuth mAuth;
    private EditText EmailEditText;
    private EditText PasswordEditText;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // things to do if already signed in
            // such as updateUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        EmailEditText = findViewById(R.id.sign_up_email);
        PasswordEditText = findViewById(R.id.sign_up_password);

        Button logInButton = findViewById(R.id.sign_up);
        Button signUpButton = findViewById(R.id.sign_up2login);

        signUpButton.setOnClickListener(v -> {
            // clear input and go to sign up activity
            EmailEditText.setText("");
            PasswordEditText.setText("");
            startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
        });

        logInButton.setOnClickListener(v -> {
            String email = EmailEditText.getText().toString();
            if(email.isEmpty()){
                Toast.makeText(LogInActivity.this, "Please Input email", Toast.LENGTH_SHORT).show();
                return;
            }
            //hard code
            String password = "testpassword";

            // real implement
            //String password = mPasswordEditText.getText().toString();

            logInWithEmail(email, password);
        });
    }


    private void logInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LogInActivity.this, "Log in successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LogInActivity.this, ContactsActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        // back to Main or home?
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(LogInActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}