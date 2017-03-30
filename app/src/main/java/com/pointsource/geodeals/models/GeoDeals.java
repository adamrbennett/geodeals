package com.pointsource.geodeals.models;

import com.google.android.gms.location.Geofence;

import java.util.HashMap;
import java.util.Map;

public class GeoDeals {
    public static final Map<String, GeoDeal> deals = new HashMap<>();

    static {
        GeoDeal museum = new GeoDeal(
                "Welcome to the Denver Museum of Nature and Science",
                "Come see our latest exhibit, Vikings: Beyond the Legend and mention GeoDeals to receive 10% off admission!");

        museum.setId("museum");
        museum.setLatitude(39.7483);
        museum.setLongitude(-104.943);
        museum.setRadius(100);
        museum.setExpirationDuration(Geofence.NEVER_EXPIRE);
        museum.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);

        GeoDeal zoo = new GeoDeal(
                "Denver Zoo",
                "Don't leave without getting your photo taken with Lumpy the Tortoise!");

        zoo.setId("zoo");
        zoo.setLatitude(39.7505);
        zoo.setLongitude(-104.95);
        zoo.setRadius(100);
        zoo.setExpirationDuration(Geofence.NEVER_EXPIRE);
        zoo.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT);

        deals.put(museum.getId(), museum);
        deals.put(zoo.getId(), zoo);
    }
}
