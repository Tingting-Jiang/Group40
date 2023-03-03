package edu.northeastern.group40.A8;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.R;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUp";

    private FirebaseAuth mAuth;
    private EditText EmailEditText;
    private EditText PasswordEditText;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a8_sign_up);

        mAuth = FirebaseAuth.getInstance();

        EmailEditText = findViewById(R.id.sign_up_email);
        PasswordEditText = findViewById(R.id.sign_up_password);


        Button signUpButton = findViewById(R.id.sign_up);
        Button logInButton = findViewById(R.id.sign_up2login);

        signUpButton.setOnClickListener(v -> {
            String email = EmailEditText.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please Input email", Toast.LENGTH_SHORT).show();
                return;
            }

            // now hardcode password to skip verification
            String password = "testpassword";

            // real implement
            // String password = mPasswordEditText.getText().toString();

            signUpWithEmail(email, password);
        });

        logInButton.setOnClickListener(v -> onBackPressed());
    }

    private void signUpWithEmail(String email, String password) {
        String userName = email.substring(0, email.indexOf("@"));
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        User newUser = new User(userName, userId, new HashMap<>(), email);
                        saveNewUserToDB(newUser);

                        Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, ContactsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Log.w("", Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveNewUserToDB(User newUser) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(newUser.getUserId());
        reference.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(SignUpActivity.this, ContactsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Failed to login with this email",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
