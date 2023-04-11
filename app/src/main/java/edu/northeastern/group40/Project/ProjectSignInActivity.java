package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.northeastern.group40.R;

public class ProjectSignInActivity extends AppCompatActivity {

    private EditText mEmailEditText;

    private EditText mPasswordEditText;

    private Button mSignInButton;

    private TextView mSignUpTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_in);

        mAuth = FirebaseAuth.getInstance();

        View rootView = findViewById(R.id.sign_in_layout);

        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(rootView);
        setContentView(scrollView);

        mEmailEditText = findViewById(R.id.email_sign_in_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);

        mSignInButton = findViewById(R.id.sign_in_button_in_sign_in_activity);
        mSignUpTextView = findViewById(R.id.sign_up_link);

        mSignInButton.setOnClickListener(v -> {
            //TODO: Check if the username and password are correct
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please input email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please input password", Toast.LENGTH_SHORT).show();
                return;
            }
            signInWithEmail(email, password);
        });

        mSignUpTextView.setOnClickListener(v -> {
           Intent intent = new Intent(ProjectSignInActivity.this, ProjectSignUpActivity.class);
           startActivity(intent);
        });
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Sign in successfully \n" + "Email: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProjectSignInActivity.this, ProjectActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


}