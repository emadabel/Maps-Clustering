package com.emadabel.mapsclustringexample;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class Person implements ClusterItem, Parcelable {
    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
    private String mTitle;
    private LatLng mPosition;
    private String mSnippet;
    private boolean mIsInfoWindowShown;

    public Person(double lat, double lng, String title, String snippet, boolean isInfoWindowShown) {
        mTitle = title;
        mSnippet = snippet;
        mPosition = new LatLng(lat, lng);
        mIsInfoWindowShown = isInfoWindowShown;
    }

    protected Person(Parcel in) {
        mTitle = in.readString();
        mPosition = in.readParcelable(LatLng.class.getClassLoader());
        mSnippet = in.readString();
        mIsInfoWindowShown = in.readByte() != 0;
    }

    public static Person fromMarker(Marker marker) {
        return new Person(marker.getPosition().latitude, marker.getPosition().longitude,
                marker.getTitle(), marker.getSnippet(), marker.isInfoWindowShown());
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public void setPosition(LatLng position) {
        this.mPosition = position;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public void setSnippet(String snippet) {
        this.mSnippet = snippet;
    }

    public boolean isIsInfoWindowShown() {
        return mIsInfoWindowShown;
    }

    public void setIsInfoWindowShown(boolean isInfoWindowShown) {
        this.mIsInfoWindowShown = isInfoWindowShown;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeParcelable(mPosition, flags);
        dest.writeString(mSnippet);
        dest.writeByte((byte) (mIsInfoWindowShown?1:0));
    }
}
