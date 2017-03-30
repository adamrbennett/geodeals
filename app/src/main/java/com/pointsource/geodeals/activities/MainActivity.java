package com.pointsource.geodeals.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.pointsource.geodeals.R;
import com.pointsource.geodeals.models.GeoDeal;
import com.pointsource.geodeals.models.GeoDeals;
import com.pointsource.geodeals.services.GeofenceTransitionsIntentService;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = MainActivity.class.getSimpleName();

    List<Geofence> geofences = new ArrayList<>();
    PendingIntent geofencePendingIntent;
    GoogleApiClient googleApiClient;
    boolean isReceivingGeofencingUpdates = false;

    CoordinatorLayout rootLayout;
    RelativeLayout deal;
    TextView title;
    TextView text;

    @DebugLog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        deal = (RelativeLayout) rootLayout.findViewById(R.id.deal);
        title = (TextView) rootLayout.findViewById(R.id.title);
        text = (TextView) rootLayout.findViewById(R.id.text);

        createGeofences();

        createGoogleApiClient();

        Intent intent = getIntent();
        if (intent != null) {
            title.setText(intent.getStringExtra("deal.title"));
            text.setText(intent.getStringExtra("deal.text"));
            deal.setVisibility(View.VISIBLE);
        }
    }

    @DebugLog
    @Override
    protected void onStart() {
        super.onStart();
        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @DebugLog
    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnecting() || googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @DebugLog
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!isReceivingGeofencingUpdates) {
            startGeofencingUpdates();
        }
    }

    @DebugLog
    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @DebugLog
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @DebugLog
    @Override
    public void onResult(Status status) {
        if (status.isSuccess())
            isReceivingGeofencingUpdates = true;
    }

    @DebugLog
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @DebugLog
    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @DebugLog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = (data != null) ? LocationSettingsStates.fromIntent(data) : null;
        switch (requestCode) {
            case 200:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        startGeofencingUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        showSnackbar(rootLayout, "Location services required", Snackbar.LENGTH_INDEFINITE, "Try again", view -> startGeofencingUpdates());
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @DebugLog
    private void startGeofencingUpdates() {
        if (!googleApiClient.isConnected()) {
            showSnackbar(rootLayout, "Google API client not connected", Snackbar.LENGTH_INDEFINITE);
            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        pendingResult.setResultCallback((result) -> {
            final Status status = result.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    Log.d(TAG, "Settings OK");
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Permissions OK");
                        Log.d(TAG, "Requesting geofence updates");

                        GeofencingRequest request = getGeofencingRequest();
                        PendingIntent intent = getGeofencePendingIntent();

                        Log.d(TAG, String.format("Geofencing request: %s", request));
                        Log.d(TAG, String.format("Geofencing intent: %s", intent));

                        LocationServices.GeofencingApi.addGeofences(
                                googleApiClient,
                                request,
                                intent
                        ).setResultCallback(this);
                    } else {
                        Log.d(TAG, "Need permissions");
                        insufficientPermissions();
                    }
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.d(TAG, "Need Settings");
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    insufficientSettings(status);
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    Log.d(TAG, "Settings cannot be satisfied");
                    break;
            }
        });
    }

    @DebugLog
    private void insufficientPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    @DebugLog
    private void insufficientSettings(Status status) {
        try {
            status.startResolutionForResult(this, 200);
        } catch (IntentSender.SendIntentException e) {
            // Ignore the error.
        }
    }

    @DebugLog
    private void createGeofences() {
        for (Map.Entry<String, GeoDeal> entry : GeoDeals.deals.entrySet()) {
            String id = entry.getKey();
            GeoDeal deal = entry.getValue();

            geofences.add(new Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(
                            deal.getLatitude(),
                            deal.getLongitude(),
                            deal.getRadius()
                    )
                    .setExpirationDuration(deal.getExpirationDuration())
                    .setTransitionTypes(deal.getTransitionTypes())
                    .build());
        }
    }

    @DebugLog
    private void createGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @DebugLog
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    @DebugLog
    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
//        // calling addGeofences() and removeGeofences().
//        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        return geofencePendingIntent;
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @DebugLog
    private void showSnackbar(View view, CharSequence msg, int duration) {
        showSnackbar(view, msg, duration, null, null);
    }

    @DebugLog
    private void showSnackbar(View view, CharSequence msg, int duration, CharSequence action, View.OnClickListener listener) {
        Snackbar snack = Snackbar.make(view, msg, duration);
        if (!TextUtils.isEmpty(action) && listener != null)
            snack.setAction(action, listener);

        snack.show();
    }
}
