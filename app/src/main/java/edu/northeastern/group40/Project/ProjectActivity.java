package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import edu.northeastern.group40.R;

public class ProjectActivity extends AppCompatActivity {
    Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        mSignInButton = findViewById(R.id.sign_in_button_in_project_activity);

        mSignInButton.setOnClickListener(v -> {
           Intent intent = new Intent(ProjectActivity.this, ProjectSignInActivity.class);
           startActivity(intent);
        });
    }
}