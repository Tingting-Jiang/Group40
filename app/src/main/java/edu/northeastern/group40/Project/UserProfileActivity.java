package edu.northeastern.group40.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.group40.R;

public class UserProfileActivity extends AppCompatActivity {

    TextView mUserNameTextView;

    TextView mUserEmailTextView;

    TextView mUserPhoneTextView;

    TextView mBalanceTextView;

    TextView mIsCarOwnerTextView;

    Button mSignOutButton;

    Button mMyCarsButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUserNameTextView = findViewById(R.id.user_profile_name);
        mUserEmailTextView = findViewById(R.id.user_profile_email);
        mUserPhoneTextView = findViewById(R.id.user_profile_phone);
        mBalanceTextView = findViewById(R.id.user_profile_balance);
        mIsCarOwnerTextView = findViewById(R.id.user_profile_is_car_owner);
        mSignOutButton = findViewById(R.id.user_profile_sign_out);
        mMyCarsButton = findViewById(R.id.user_profile_my_car);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

//      FirebaseApp.initializeApp(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        displayText(mUserNameTextView, databaseReference, "username");
        displayText(mUserEmailTextView, databaseReference, "email");
        displayText(mUserPhoneTextView, databaseReference, "phone");
        displayText(mBalanceTextView, databaseReference, "balance");
        displayText(mIsCarOwnerTextView, databaseReference, "carRenter");

        changeInfo(mUserNameTextView, databaseReference, "username", "username");
        changeInfo(mUserEmailTextView, databaseReference, "email", "email");
        changeInfo(mUserPhoneTextView, databaseReference, "phone", "phone");

        changeCarOwnerStatus(mIsCarOwnerTextView, databaseReference);

        mSignOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
        });

        mMyCarsButton.setOnClickListener(v -> {
            if (mIsCarOwnerTextView.getText().toString().equals("No")) {
                AlertDialog alertDialog = new AlertDialog.Builder(UserProfileActivity.this)
                        .setTitle("You are not a car owner")
                        .setMessage("To view your cars, change your car ownership status to 'Yes'")
                        .setPositiveButton("OK", null)
                        .create();
                alertDialog.show();

            } else {
                Intent intent = new Intent(UserProfileActivity.this, MyCarsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayText(TextView textView, DatabaseReference databaseReference, String childName) {
        databaseReference.child(childName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = String.valueOf(snapshot.getValue());
                    if (childName.equals("carRenter")) {
                        if (value.equals("true")) {
                            textView.setText("Yes");
                        } else {
                            textView.setText("No");
                        }
                    } else if (childName.equals("balance")) {
                        textView.setText(value + "$");
                    } else {
                            textView.setText(value);
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, childName + "not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeInfo(TextView textView, DatabaseReference databaseReference, String childName, String filedName) {

        textView.setOnClickListener(v -> {
            EditText input = new EditText(this);

            AlertDialog alertDialog = new AlertDialog.Builder(UserProfileActivity.this)
                    .setTitle("Change " + filedName)
                    .setMessage("Enter new " + filedName)
                    .setView(input)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String value = input.getText().toString();
                        if (childName.equals("email")) {
                            if(!value.contains("@") || !value.contains(".")) {
                                Toast.makeText(UserProfileActivity.this, "Invalid email. Change failed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updateEmail(value);
                        }
                        if (childName.equals("phone")) {
                            if (value.length() != 10) {
                                Toast.makeText(UserProfileActivity.this, "Invalid phone number. Change failed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        databaseReference.child(childName).setValue(value);
                        displayText(textView, databaseReference, childName);
                        input.setOnFocusChangeListener(null);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                        input.setOnFocusChangeListener(null);
                    })
                    .create();
            alertDialog.show();

            // Move the removeView() call to after the show() method
            alertDialog.setOnShowListener(dialog -> {
                ViewGroup parent = (ViewGroup) input.getParent();
                if (parent != null) {
                    parent.removeView(input);
                }
            });
        });
    }

    private void changeCarOwnerStatus(TextView textView, DatabaseReference databaseReference) {
        textView.setOnClickListener(v -> {
            databaseReference.child("carRenter").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isCarOwner = snapshot.getValue(Boolean.class);
                    AlertDialog alertDialog = new AlertDialog.Builder(UserProfileActivity.this)
                            .setTitle("Change car ownership status")
                            .setMessage("Are you sure that you want to change your car ownership status?")
                            .setPositiveButton("OK", (dialog, which) -> {
                                databaseReference.child("carRenter").setValue(!isCarOwner);
                                displayText(textView, databaseReference, "carRenter");
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .create();
                    alertDialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


}