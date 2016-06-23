package com.hodachop93.hohoda.fragment;


import android.Manifest;
import android.app.FragmentManager;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.adapter.InfoWindowPagerAdapter;
import com.hodachop93.hohoda.eventbus.LocationEventBus;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.MultiDrawable;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MapViewFragment.class.getSimpleName();
    private static final float DEFAULT_CAMERA_ZOOM = 12.5f;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 2000;
    private static final float MIN_DISTANCE = 10.0f;
    private static final int PERMISSION_ACCESS_LOCATION = 101;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.sliding_up)
    SlidingUpPanelLayout slidingUpPanel;


    private List<Profile> mTutors;
    private ClusterManager mClusterManager;


    private GoogleMap mMap;
    private LatLng mUserLocation;

    private View coveredView;
//    private Marker mCenterMarker = null;

    //     Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private InfoWindowPagerAdapter mInfoWindowAdapter;
    private long lastClickMillis = 0;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkGooglePlaceServices()) {
            buildGoogleApiClient();

            if (mUserLocation == null)
                createLocationRequest();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, view);

        slidingUpPanel.setPanelSlideListener(mPanelSlideListener);
        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                if (isAdded()) {
                    FragmentManager fm = getChildFragmentManager();
                    MapFragment mapFragment = MapFragment.newInstance();

                    try {
                        fm.beginTransaction()
                                .add(R.id.map_container, mapFragment).commit();
                        mapFragment.getMapAsync(MapViewFragment.this);
                    } catch (IllegalStateException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });


        Utils.setupUIForAutoHideKeyboard(getActivity(), view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (slidingUpPanel != null)
            slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        LocationEventBus.getInstance().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onLocationEventBus(LocationEventBus.Event event) {
        getUserLocation();
    }

    @Override
    public void onStop() {
        LocationEventBus.getInstance().unregister(this);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();

    }

    public void setCoveredView(View view) {
        this.coveredView = view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mClusterManager = new ClusterManager<Profile>(getActivity(), mMap);
        mClusterManager.setRenderer(new PersonRenderer());
        mMap.setOnCameraChangeListener(mClusterManager);

        setUpMap(mMap);
        addMarker();
    }

    private void setUpMap(GoogleMap mMap) {
        mMap.setIndoorEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        }

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    // request Location Permission on Android M
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ACCESS_LOCATION);
    }

    public void setData(List<Profile> tutors) {
        this.mTutors = tutors;

        if (mClusterManager != null) {
            addMarker();
        }

        if (slidingUpPanel != null)
            slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    private void addMarker() {

        mClusterManager.clearItems();
        //add cluster item
        if (mTutors != null) {
            for (Profile tutor : mTutors) {
                if (tutor != null && tutor.getPosition() != null) {
                    mClusterManager.addItem(tutor);
                }
            }
        }

        mClusterManager.cluster();

        //add camera point to zoom
        zoomCameraToPoint();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private boolean checkGooglePlaceServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(getActivity());
        if (code == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(code) &&
                api.showErrorDialogFragment(getActivity(), code, REQUEST_GOOGLE_PLAY_SERVICES)) {
            return false;
        } else {
            String str = GoogleApiAvailability.getInstance().getErrorString(code);
            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
            return false;
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    // show location dialog like Google Maps
    protected void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {

                if (!isAdded()) return;

                final Status status = result.getStatus();
                final LocationSettingsStates stae = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here.
                        getUserLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(), REQUEST_GOOGLE_PLAY_SERVICES);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void getUserLocation() {

        if (mUserLocation != null)
            return;


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (userLocation != null) {
                double lat = userLocation.getLatitude();
                double lng = userLocation.getLongitude();
                mUserLocation = new LatLng(lat, lng);
            }
        }
        zoomCameraToPoint();
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Check if user dup-click
        long currentMillis = System.currentTimeMillis();
        if (currentMillis - lastClickMillis < 1000) {
            return false;
        }
        lastClickMillis = currentMillis;

        List<Profile> tutorsNearMarker = getTutorsNearByMarker(marker);
        if (tutorsNearMarker == null) {
            return false;
        }

        if (mInfoWindowAdapter == null) {
            mInfoWindowAdapter = new InfoWindowPagerAdapter(getChildFragmentManager(), tutorsNearMarker);
            viewPager.setAdapter(mInfoWindowAdapter);
        } else {
            mInfoWindowAdapter.setData(tutorsNearMarker);
        }

        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        return false;
    }

    private List<Profile> getTutorsNearByMarker(Marker marker) {
        LatLng latLng = marker.getPosition();
        List<Profile> filterdTutors = null;
        for (Profile item : mTutors) {
            if (item.getPosition() == null) {
                continue;
            }
            if (computeDistanceBetween(latLng, item.getPosition()) < MIN_DISTANCE) {
                if (filterdTutors == null) {
                    filterdTutors = new ArrayList<>();
                }
                filterdTutors.add(item);
            }
        }
        return filterdTutors;
    }

    private float computeDistanceBetween(LatLng pos1, LatLng pos2) {
        float[] result = new float[1];
        Location.distanceBetween(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude, result);
        return result[0];
    }

    public LatLng getFirstTutorLocation() {
        if (mTutors == null || mTutors.isEmpty())
            return null;

        for (Profile job : mTutors) {
            if (job != null && job.getPosition() != null)
                return job.getPosition();
        }

        return null;
    }

    private SlidingUpPanelLayout.PanelSlideListener mPanelSlideListener = new SlidingUpPanelLayout.PanelSlideListener() {

        @Override
        public void onPanelSlide(View view, float v) {
            if (coveredView != null)
                coveredView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPanelCollapsed(View view) {
            if (coveredView != null)
                coveredView.setVisibility(View.GONE);
        }

        @Override
        public void onPanelExpanded(View view) {
            if (coveredView != null)
                coveredView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPanelAnchored(View view) {
            if (coveredView != null)
                coveredView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPanelHidden(View view) {
            if (coveredView != null)
                coveredView.setVisibility(View.GONE);
        }
    };


    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            createLocationRequest();
            return true;
        }
    };

    private void zoomCameraToPoint() {
        LatLng cameraPoint;
        if (mUserLocation != null) {
            cameraPoint = mUserLocation;
        } else {
            cameraPoint = getFirstTutorLocation();
        }
        if (mMap != null && cameraPoint != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPoint, DEFAULT_CAMERA_ZOOM));
        }


    }

    public GoogleMap getMap() {
        return mMap;
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PersonRenderer extends DefaultClusterRenderer<Profile> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getActivity(), getMap(), mClusterManager);

            View multiProfile = LayoutInflater.from(getActivity()).inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getActivity());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(final Profile person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageDrawable(person.getAvatar());
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.getName());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Profile> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Profile p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = p.getAvatar();
                if (drawable != null) {
                    drawable.setBounds(0, 0, width, height);
                    profilePhotos.add(drawable);
                }
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }


}
