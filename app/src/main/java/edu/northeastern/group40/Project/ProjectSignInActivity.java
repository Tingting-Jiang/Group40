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

import edu.northeastern.group40.R;

public class ProjectSignInActivity extends AppCompatActivity {

    private EditText mUsernameEditText;

    private EditText mPasswordEditText;

    private Button mSignInButton;

    private TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_in);

        View rootView = findViewById(R.id.sign_in_layout);

        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(rootView);
        setContentView(scrollView);

        mUsernameEditText = findViewById(R.id.username_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);

        mSignInButton = findViewById(R.id.sign_in_button_in_sign_in_activity);
        mSignUpTextView = findViewById(R.id.sign_up_link);

        mSignInButton.setOnClickListener(v -> {
            //TODO: Check if the username and password are correct
            Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show();
        });

        mSignUpTextView.setOnClickListener(v -> {
           Intent intent = new Intent(ProjectSignInActivity.this, ProjectSignUpActivity.class);
           startActivity(intent);
        });
    }
}