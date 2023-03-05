package edu.northeastern.group40.A8;



import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import edu.northeastern.group40.A8.Models.Message;
import edu.northeastern.group40.R;

public class NotificationListener implements ChildEventListener {

    private final String userId;
    private final Context context;

    public NotificationListener(String userId, Context context){
        this.userId=userId;
        this.context=context;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        Message lastMsg = snapshot.getValue(Message.class);
        assert lastMsg != null;
        if (lastMsg.getReceiver() != null && lastMsg.getReceiver().equals(userId)) {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("friendUserId", lastMsg.getSender());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            String stickerID = lastMsg.getMessage();

            int id = context.getResources().getIdentifier(stickerID, "drawable", context.getPackageName());

            Bitmap graph = BitmapFactory.decodeResource(context.getResources(), id);

            NotificationCompat.Builder builder = new NotificationCompat
                    .Builder(context, "default")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("New message from " + lastMsg.getSenderFullName())
                    .setLargeIcon(graph)
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

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
