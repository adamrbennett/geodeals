package com.pointsource.geodeals.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.pointsource.geodeals.R;
import com.pointsource.geodeals.activities.MainActivity;
import com.pointsource.geodeals.models.GeoDeal;
import com.pointsource.geodeals.models.GeoDeals;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @DebugLog
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @DebugLog
    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @DebugLog
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @DebugLog
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @DebugLog
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.e(TAG, String.format("Geofencing error: %d", event.getErrorCode()));
            return;
        }

        // Get the transition type.
        int geofenceTransition = event.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the deal
            List<GeoDeal> deals = getDeals(event);

            // Send notifications
            for (GeoDeal deal : deals)
                sendNotification(deal);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    @DebugLog
    private List<GeoDeal> getDeals(GeofencingEvent event) {
        List<GeoDeal> deals = new ArrayList<>();
        for (Geofence geofence : event.getTriggeringGeofences()) {
            deals.add(GeoDeals.deals.get(geofence.getRequestId()));
        }

        return deals;
    }

    @DebugLog
    private void sendNotification(GeoDeal deal) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_beenhere_white_48dp)
                        .setContentTitle(deal.getTitle())
                        .setContentText(deal.getText())
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(deal.getText()));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("deal.title", deal.getTitle());
        resultIntent.putExtra("deal.text", deal.getText());

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }
}
