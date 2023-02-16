package edu.northeastern.group40;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.group40.A6.A6Activity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onA6Activity(View view) {
        openNewActivity(A6Activity.class);
    }

    private void openNewActivity(Class newActivity) {
        Intent intent = new Intent (MainActivity.this, newActivity);
        startActivity(intent);
    }
}