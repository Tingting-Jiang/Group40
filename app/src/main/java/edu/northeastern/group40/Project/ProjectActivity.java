package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.group40.MainActivity;
import edu.northeastern.group40.R;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

    }

    public void onCarListActivity(View view) {
        Intent intent = new Intent(ProjectActivity.this, CarListActivity.class);
        intent.putExtra("filter-type", "SUV");
        intent.putExtra("filter-date", "April 23-27");
        intent.putExtra("filter-price", true);
    }
    public void onAddVehicleActivity(View view) {
        openNewActivity(AddVehicleActivity.class);
    }


    private void openNewActivity(Class targetActivityClass) {
        Intent intent = new Intent (ProjectActivity.this, targetActivityClass);
        startActivity(intent);
    }

    public void openSearchActivity(View view) {
        Intent intent = new Intent (ProjectActivity.this, SearchActivity.class);
        startActivity(intent);
    }
}