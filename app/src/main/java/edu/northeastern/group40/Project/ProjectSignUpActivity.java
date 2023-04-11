package edu.northeastern.group40.Project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import edu.northeastern.group40.Project.Models.User;
import edu.northeastern.group40.R;

public class ProjectSignUpActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private EditText mPhoneEditText;
    private Button mSignUpButton;
    private TextView mSignInLink;

    private CheckBox mIsCarOwnerCheckBox;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_up);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        View rootView = findViewById(R.id.sign_up_layout);

        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(rootView);
        setContentView(scrollView);

        // Bind UI elements to Java variables
        mUsernameEditText = findViewById(R.id.email_sign_in_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPhoneEditText = findViewById(R.id.phone_number_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mIsCarOwnerCheckBox = findViewById(R.id.is_car_owner_checkbox);

        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignInLink = findViewById(R.id.sign_in_link);

        // Set click listener for sign up button
        mSignUpButton.setOnClickListener(v -> {
            if (checkValidInput()) {
                writeNewUser(mUsernameEditText.getText().toString(),
                        mPasswordEditText.getText().toString(),
                        mEmailEditText.getText().toString(),
                        mPhoneEditText.getText().toString(),
                        mIsCarOwnerCheckBox.isChecked());
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

        return valid;
    }

    private void writeNewUser(String username, String password, String email, String phone, boolean isCarOwner) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String uid = Objects.requireNonNull(currentUser).getUid();
                        User user = new User(username, password, email, phone, isCarOwner, uid);
                        saveUserToDatabase(user);
                        Toast.makeText(this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProjectSignUpActivity.this, ProjectActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Exception error = task.getException();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Sign up failed")
                                .setMessage(error.getMessage())
                                .setPositiveButton("OK", null)
                                .create()
                                .show();
                    }
                });
    }

    private void saveUserToDatabase(User user) {
        DatabaseReference reference = mDatabase.child("users").child(user.getUserID());
        reference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(ProjectSignUpActivity.this, ProjectSignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(ProjectSignUpActivity.this, "Failed to sign up with this email",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}