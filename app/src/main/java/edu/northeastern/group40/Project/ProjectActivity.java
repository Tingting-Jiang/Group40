package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import android.view.View;

import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.PriceOrder;
import edu.northeastern.group40.Project.Models.VehicleBodyStyle;
import edu.northeastern.group40.R;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
    }


    private void openNewActivity(Class targetActivityClass) {
        Intent intent = new Intent (ProjectActivity.this, targetActivityClass);
        startActivity(intent);
    }
    public void onAddVehicleActivity(View view) {
        openNewActivity(AddVehicleActivity.class);
    }
    public void onMyOrdersActivity(View view) {
        openNewActivity(MyOdersActivity.class);
    }
    public void openSearchActivity(View view) {
        openNewActivity(SearchActivity.class);
    }
    public void onProfileActivity(View view) {
        openNewActivity(MyProfile.class);
    }
}