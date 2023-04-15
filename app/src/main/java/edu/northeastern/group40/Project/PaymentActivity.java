package edu.northeastern.group40.Project;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.Order;
import edu.northeastern.group40.Project.Models.User;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;

public class PaymentActivity extends AppCompatActivity {
    private Vehicle orderVehicle;
    private int rentLength;
    private int orderPriceTotal;
    private AvailableDate availableDate;
    private Order order;
    private String orderId;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currUser;
    private String currUserId;
    private int currentBalance;
    private int ownerBalance;
    private String ownerId;
    private TextView tvBrand, tvModel, tvStartDate, tvEndDate, tvPrice, tvCurrentBalance, tvDays, tvAllPrice;
    private Button btnProcess;
    private Button btnExit;
    private DatabaseReference usersDB;
    private DatabaseReference ordersDB;
    private DatabaseReference vehicleDB;
    @Nullable
    private User owner;
    @Nullable
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        orderVehicle = (Vehicle) getIntent().getSerializableExtra("carDetail");
        rentLength = getIntent().getIntExtra("rentLength", 0);
        availableDate = (AvailableDate) getIntent().getSerializableExtra("targetDate");
        ownerId = orderVehicle.getOwnerID();
        initDatabases();
        initUI();
        btnProcess.setOnClickListener(v -> {
            if(currentBalance >= orderPriceTotal){
                orderToDb();
                processCurBalance();
                increaseBalance();
                writeOrderToDb();

            } else{
                processFailed();
            }
        });
        btnExit.setOnClickListener(v -> openNewActivity(ProjectActivity.class));
    }

    @SuppressLint("SetTextI18n")
    private void initUI(){
        tvBrand = findViewById(R.id.tv_order_brand);
        tvModel = findViewById(R.id.tv_order_model);
        tvStartDate = findViewById(R.id.start_date);
        tvEndDate = findViewById(R.id.end_date);
        tvPrice = findViewById(R.id.price);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        tvDays = findViewById(R.id.tv_days);
        tvAllPrice = findViewById(R.id.tv_price_all);
        btnProcess = findViewById(R.id.btn_process);
        btnExit = findViewById(R.id.btn_exit);

        tvBrand.setText(orderVehicle.getBrand().toString());
        tvModel.setText(orderVehicle.getModel().toString());
        tvStartDate.setText(availableDate.getStartDate());
        tvEndDate.setText(availableDate.getEndDate());
        tvPrice.setText(orderVehicle.getRentPrice()+"$");

        usersDB.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentBalance = dataSnapshot.child(currUserId).child("balance").getValue(Integer.class);
                tvCurrentBalance.setText(String.valueOf(currentBalance) + "$");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, "Fail to get balance data.", Toast.LENGTH_SHORT).show();
            }
        });
        tvDays.setText(String.valueOf(rentLength));
        orderPriceTotal = orderVehicle.getRentPrice() * rentLength;
        tvAllPrice.setText(String.valueOf(orderPriceTotal)+"$");
    }

    private void initDatabases() {
        usersDB = mDatabase.child("users");
        ordersDB = mDatabase.child("orders");
        vehicleDB = mDatabase.child("vehicles");
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;
        currUserId = currUser.getUid();
    }

    private void orderToDb(){
        orderId = mDatabase.push().getKey();
        order = new Order(orderId, orderVehicle, availableDate, orderPriceTotal, currUserId);
        assert orderId != null;
        ordersDB.child(orderId).setValue(order);
    }

    private void processCurBalance(){
        currentBalance -= orderPriceTotal;
        usersDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersDB.child(currUserId).child("balance").setValue(currentBalance);
                tvCurrentBalance.setText(String.valueOf(currentBalance) + "$");

                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setMessage("Your order is completed.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openNewActivity(ProjectActivity.class);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, "Fail to get balance data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processFailed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setMessage("Your account balance is insufficient.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void increaseBalance(){
        usersDB.child(ownerId).child("balance").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    ownerBalance = task.getResult().getValue(Integer.class);
                    ownerBalance += orderPriceTotal;
                    usersDB.child(ownerId).child("balance").setValue(ownerBalance);
                }
            }
        });
    }

    private void openNewActivity(Class targetActivityClass) {
        Intent intent = new Intent (PaymentActivity.this, targetActivityClass);
        startActivity(intent);
    }

    private void writeOrderToDb(){
        usersDB.child(ownerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    List ownersOrdersAsCarOwner = new ArrayList<Order>();
                    for(DataSnapshot snap: task.getResult().child("ordersAsCarOwner").getChildren()){
                        ownersOrdersAsCarOwner.add(snap.getValue(Order.class));
                    }
                    ownersOrdersAsCarOwner.add(order);
                    usersDB.child(ownerId).child("ordersAsCarOwner").setValue(ownersOrdersAsCarOwner);
                }
            }
        });
        usersDB.child(currUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else{
                    List usersOrdersAsCarUser = new ArrayList<Order>();
                    for(DataSnapshot snap: task.getResult().child("ordersAsCarUser").getChildren()){
                        usersOrdersAsCarUser.add(snap.getValue(Order.class));
                    }
                    usersOrdersAsCarUser.add(order);
                    usersDB.child(currUserId).child("ordersAsCarUser").setValue(usersOrdersAsCarUser);
                }
            }
        });

        Vehicle currVehicle = order.getOrderedVehicle();
        currVehicle.setAvailable(false);

        vehicleDB.child(order.getOrderedVehicle().getVehicleID()).setValue(currVehicle);
    }
}