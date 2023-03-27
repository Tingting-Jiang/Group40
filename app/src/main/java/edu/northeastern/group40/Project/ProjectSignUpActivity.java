package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import edu.northeastern.group40.R;

public class ProjectSignUpActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private EditText mPhoneEditText;
    private Button mSignUpButton;
    private TextView mSignInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_up);

        // Bind UI elements to Java variables
        mUsernameEditText = findViewById(R.id.username_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPhoneEditText = findViewById(R.id.phone_number_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);

        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignInLink = findViewById(R.id.sign_in_link);

        // Set click listener for sign up button
        mSignUpButton.setOnClickListener(v -> {
            if (checkValidInput()) {
                // TODO: Write to database
            }
        });

        // Set click listener for sign in link
        mSignInLink.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectSignUpActivity.this, ProjectSignInActivity.class);
            startActivity(intent);
        });
    }

    private boolean checkValidInput() {
        boolean valid = true;
        if (mUsernameEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input username", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (mEmailEditText.getText().toString().isEmpty() || !mEmailEditText.getText().toString().contains("@")) {
            Toast.makeText(this, "Please input valid email", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (mPhoneEditText.getText().toString().isEmpty() || mPhoneEditText.getText().toString().length() != 10
        || !mPhoneEditText.getText().toString().matches("^\\d+")){
            Toast.makeText(this, "Please input valid phone number", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (mPasswordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input valid password", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (valid) {
            String successUserName = "Username: " + mUsernameEditText.getText().toString() + "\n";
            String successEmail = "Email: " + mEmailEditText.getText().toString() + "\n";
            String successPhone = "Phone: " + mPhoneEditText.getText().toString() + "\n";
            String successPassword = "Password: " + mPasswordEditText.getText().toString();

            Toast.makeText(this, successUserName, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, successEmail, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, successPhone, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, successPassword, Toast.LENGTH_SHORT).show();
        }

        return valid;
    }
}