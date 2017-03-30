package com.pointsource.geodeals.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GeoDeal implements Parcelable {

    private String id;
    private double latitude;
    private double longitude;
    private float radius;
    private long expirationDuration;
    private int transitionTypes;

    private String title;
    private String text;

    public GeoDeal(String title, String text) {
        this.title = title;
        this.text = text;
    }

    private GeoDeal(Parcel in) {
        id = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readFloat();
        expirationDuration = in.readLong();
        transitionTypes = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeFloat(radius);
        out.writeLong(expirationDuration);
        out.writeInt(transitionTypes);
    }

    public static final Parcelable.Creator<GeoDeal> CREATOR = new Parcelable.Creator<GeoDeal>() {

        @Override
        public GeoDeal createFromParcel(Parcel in) {
            return new GeoDeal(in);
        }

        @Override
        public GeoDeal[] newArray(int size) {
            return new GeoDeal[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public long getExpirationDuration() {
        return expirationDuration;
    }

    public void setExpirationDuration(long expirationDuration) {
        this.expirationDuration = expirationDuration;
    }

    public int getTransitionTypes() {
        return transitionTypes;
    }

    public void setTransitionTypes(int transitionTypes) {
        this.transitionTypes = transitionTypes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
