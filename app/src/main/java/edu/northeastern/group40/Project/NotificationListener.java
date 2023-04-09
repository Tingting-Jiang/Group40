package edu.northeastern.group40.Project;

import android.app.PendingIntent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import edu.northeastern.group40.Project.Models.Order;
import edu.northeastern.group40.Project.Models.User;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;

import android.content.Intent;
import android.widget.Toast;

import java.util.List;

public class NotificationListener implements ChildEventListener {
    private final String userId;
    private final Context context;

    public NotificationListener(String userId, Context context){
        this.userId = userId;
        this.context = context;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        User user = snapshot.getValue(User.class);
        assert user != null;
        if(user.getOrderAsCarOwner()!=null) {
            List<Order> AllOrder = user.getAllOrder();
            Order lstOrder = AllOrder.get(-1);
            Vehicle vehicle = lstOrder.getOrderedVehicle();
            Intent intent = new Intent(context, ProjectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            if(vehicle.getOwnerID().equals(userId)){
                NotificationCompat.Builder builder = new NotificationCompat
                        .Builder(context, "default")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Your car has been rented out:" + vehicle.getBrand() + "-" + vehicle.getModel())
                        .setContentText("Your account has been credited with " + lstOrder.getOrderPriceTotal() + " dollars")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat
                                .from(context);
                try {
                    int NOTIFICATION_ID = 1;
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                } catch (SecurityException e) {
                    Toast.makeText(context, "Get DB error: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
