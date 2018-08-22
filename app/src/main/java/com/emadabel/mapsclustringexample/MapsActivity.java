package com.emadabel.mapsclustringexample;

import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String CAMERA_POSITION_INSTANCE = "camera_position";
    private static final String CACHED_ITEMS_INSTANCE = "cached_items";
    private static final String MARKER_ITEM_INSTANCE = "marker_item";

    private GoogleMap mMap;
    private ClusterManager<Person> mClusterManager;
    private CameraPosition mCameraPosition;
    private List<Person> mCachedMarkers;
    private DefaultClusterRenderer<Person> renderer;
    private Marker mMarker;
    private Person mClickedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // retrieve all saved instances
        if (savedInstanceState != null) {
            mCameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION_INSTANCE);
            mCachedMarkers = savedInstanceState.getParcelableArrayList(CACHED_ITEMS_INSTANCE);
            mClickedMarker = savedInstanceState.getParcelable(MARKER_ITEM_INSTANCE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save camera position
        if (mMap != null) {
            outState.putParcelable(CAMERA_POSITION_INSTANCE, mMap.getCameraPosition());
        }

        // all markers to initialize the cluster again
        if (mCachedMarkers != null) {
            outState.putParcelableArrayList(CACHED_ITEMS_INSTANCE, new ArrayList<>(mCachedMarkers));
        }

        // clicked marker so we can show info window again
        if (mClickedMarker != null) {
            outState.putParcelable(MARKER_ITEM_INSTANCE, mClickedMarker);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // move the camera to the default position or move to last position before rotating the screen
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.167616, 28.079329), 10));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // setup the map and clustering
        mMap.clear();
        mClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        // add markers
        addPersonItems();

        renderer = new CustomClusterRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        mClusterManager.cluster();
        ////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////
        // started delayed thread to give a time to render to complete its job before showing
        // the info window
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mClickedMarker != null && renderer != null) {
                            mMarker = renderer.getMarker(mClickedMarker);
                            mMarker.showInfoWindow();
                        }
                    }
                });
            }
        });
        th.start();
        ////////////////////////////////////////////////////////////////////////////////////////////

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Person>() {
            @Override
            public boolean onClusterItemClick(Person person) {
                // save clicked marker to get info window of it later
                mClickedMarker = person;
                mMarker = renderer.getMarker(mClickedMarker);
                mMarker.showInfoWindow();
                return true;
            }
        });
    }

    private void addPersonItems() {
        if (mCachedMarkers == null) {
            /*
             *  adding fake items to cluster, noramlly we should loading from internet or database
             */
            mCachedMarkers = fetchingUtility();
        }

        mClusterManager.addItems(mCachedMarkers);
    }

    // here should be the source where we getting the data (Internet, Database, ...)
    private List<Person> fetchingUtility() {
        List<Person> fetchedList = new ArrayList<>();
        fetchedList.add(new Person(-26.220616, 28.079329, "PJ0", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.187616, 28.079329, "PJ1", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.207616, 28.079329, "PJ2", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.217616, 28.079329, "PJ3", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.346316, 28.079329, "PJ4", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.215896, 28.079329, "PJ5", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.215436, 28.079129, "PJ6", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.431461, 28.079329, "PJ7", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.168879, 28.079329, "PJ8", "https://twitter.com/pjapplez", false));
        fetchedList.add(new Person(-26.227616, 28.079329, "PJ9", "https://twitter.com/pjapplez", false));

        return fetchedList;
    }
}
