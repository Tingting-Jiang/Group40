package edu.northeastern.group40.Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.MyLocation;
import edu.northeastern.group40.R;

public class ProjectActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(ProjectActivity.this, ProjectSignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        }else{
            FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("ordersAsCarOwner").addValueEventListener(new NotificationListener(user.getUid(),this));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
    }

    public void onCarListActivity(View view) {
        Intent intent = new Intent(ProjectActivity.this, CarListActivity.class);
//        intent.putExtra("VehicleModel", Brand.Model.ACCORD.toString());
//        intent.putExtra("VehicleBrand", Brand.HONDA.toString());
        intent.putExtra("AvailableDate", new AvailableDate("04/14/2023", "04/21/2023"));
        try {
            intent.putExtra("destinationLocation", new MyLocation(37.50273, -121.95154, this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    public void onAddVehicleActivity(View view) {
        openNewActivity(AddVehicleActivity.class);
    }


    private void openNewActivity(Class targetActivityClass) {
        Intent intent = new Intent(ProjectActivity.this, targetActivityClass);
        startActivity(intent);
    }

    public void openSearchActivity(View view) {
        openNewActivity(SearchActivity.class);
    }

    public void onMyOrdersActivity(View view) {
        openNewActivity(MyOrdersActivity.class);
    }

    public void onProfileActivity(View view){
        openNewActivity(UserProfileActivity.class);
    }
}