package edu.northeastern.group40;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.group40.A6.A6Activity;
import edu.northeastern.group40.A8.LogInActivity;
import edu.northeastern.group40.Project.ProjectActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onA6Activity(View view) {
        openNewActivity(A6Activity.class);
    }

    public void onA8Activity(View view) {
        openNewActivity(LogInActivity.class);
    }

    public void onAboutActivity(View view) {
        openNewActivity(AboutActivity.class);
    }

    public void onProjectActivity(View view) {
        openNewActivity(ProjectActivity.class);
    }

    private void openNewActivity(Class targetActivityClass) {
        Intent intent = new Intent (MainActivity.this, targetActivityClass);
        startActivity(intent);
    }
}