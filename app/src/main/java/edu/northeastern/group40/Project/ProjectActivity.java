package edu.northeastern.group40.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.R;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
    }

    public void onCarListActivity(View view) {
        Intent intent = new Intent(ProjectActivity.this, CarListActivity.class);
        intent.putExtra("VehicleModel", Brand.Model.ACCORD.toString());
        intent.putExtra("VehicleBrand", Brand.HONDA.toString());
        intent.putExtra("AvailableDate", new AvailableDate("04/14/2023", "04/21/2023"));
        startActivity(intent);
    }
    public void onAddVehicleActivity(View view) {
        openNewActivity(AddVehicleActivity.class);
    }


    private void openNewActivity(Class targetActivityClass) {
        Intent intent = new Intent (ProjectActivity.this, targetActivityClass);
        startActivity(intent);
    }

    public void onSignInActivity(View view) {
        openNewActivity(ProjectSignInActivity.class);
    }

    public void openSearchActivity(View view) {
        openNewActivity(SearchActivity.class);
    }

    public void onMyOrdersActivity(View view) {
        openNewActivity(MyOrdersActivity.class);
    }
}