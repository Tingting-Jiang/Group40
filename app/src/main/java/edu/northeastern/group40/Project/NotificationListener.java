package edu.northeastern.group40.Project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.group40.Project.Models.Order;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;

import android.content.Intent;
import android.widget.Toast;



public class NotificationListener implements ValueEventListener {
    private final String userId;
    private final Context context;

    public NotificationListener(String userId, Context context){
        this.userId = userId;
        this.context = context;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Order lstOrder = null;
        for(DataSnapshot snap: snapshot.getChildren()){
            lstOrder = snap.getValue(Order.class);
        }
        if(lstOrder != null){
            Vehicle vehicle = lstOrder.getOrderedVehicle();
            NotificationCompat.Builder builder = new NotificationCompat
                    .Builder(context, "2")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Your car has been rented out:" + vehicle.getBrand() + "-" + vehicle.getModel())
                    .setContentText("Your account has been credited with " + lstOrder.getOrderPriceTotal() + " dollars")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("2", "channel2", importance);
            NotificationManager notificationManager = this.context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationManagerCompat manager = NotificationManagerCompat.from(this.context);
            System.out.println(lstOrder.getOrderId());
            try {
            int NOTIFICATION_ID = 1;
            manager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            Toast.makeText(context, "Get DB error: " + e, Toast.LENGTH_SHORT).show();
        }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
