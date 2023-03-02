package edu.northeastern.group40.A8;

import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.R;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUp";

    private FirebaseAuth mAuth;
    private EditText EmailEditText;
    private EditText PasswordEditText;
    private DatabaseReference mDatabase;

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
            if(email.isEmpty()){
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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
//                      Store the successful register user to realtime database
                        String userId = user.getUid();
                        writeNewUser(email, userId);

                        Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Log.w("", Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void writeNewUser(String email, String userId) {
        String name = email.substring(0, email.indexOf("@"));
        User user = new User(email, userId, name);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userId).setValue(user);
    }

}
