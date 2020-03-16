package com.onlyvtc.ui.activity.location_pick;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.onlyvtc.R;
import com.onlyvtc.base.BaseActivity;
import com.onlyvtc.common.Constants;
import com.onlyvtc.data.network.model.AddressResponse;
import com.onlyvtc.data.network.model.UserAddress;
import com.onlyvtc.ui.adapter.PlacesAutoCompleteAdapter;

import org.json.JSONArray;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onlyvtc.MvpApplication.RIDE_REQUEST;
import static com.onlyvtc.MvpApplication.canGoToChatScreen;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.STOP_ADD;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.STOP_LANG;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.STOP_LAT;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.WAY_LOCATIONS;

public class LocationPickActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationPickIView, PlaceClickListener {

    private static LatLng BOUNDS = new LatLng(0, 0);
    private Location mLastKnownLocation;

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.source)
    EditText source;
    @BindView(R.id.destination)
    EditText destination;
    @BindView(R.id.stop)
    EditText stop;
    @BindView(R.id.destination_layout)
    LinearLayout destinationLayout;
    @BindView(R.id.home_address_layout)
    LinearLayout homeAddressLayout;
    @BindView(R.id.work_address_layout)
    LinearLayout workAddressLayout;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.locations_rv)
    RecyclerView locationsRv;
    @BindView(R.id.location_bs_layout)
    CardView locationBsLayout;
    @BindView(R.id.dd)
    CoordinatorLayout dd;
    boolean isEnableIdle = false;
    @BindView(R.id.llSource)
    LinearLayout llSource;
    @BindView(R.id.stop_layout)
    LinearLayout stopLayout;

    @BindView(R.id.add_stop)
    ImageView addStop;

    @BindView(R.id.marker_pin)
    ImageView markerPin;

    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private boolean mLocationPermissionGranted;
    private boolean isStop = false;
    private GoogleMap mGoogleMap;
    private String s_address;
    private Double s_latitude;
    private Double s_longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean isEditable = true;
    private UserAddress home, work = null;
    private LocationPickPresenter<LocationPickActivity> presenter = new LocationPickPresenter<>();
    private EditText selectedEditText;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    //Base on action we are show/hide view and setResults
    private String actionName = Constants.LocationActions.SELECT_SOURCE;
    private int mIsGOTGPSADDRESSWHENLOAD = -1;

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

            if (mIsGOTGPSADDRESSWHENLOAD == 1) {
                mIsGOTGPSADDRESSWHENLOAD = 0;
                return;
            }
            if (isEditable) if (!s.toString().equals("")) {
                locationsRv.setVisibility(View.VISIBLE);
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            if (s.toString().equals("")) locationsRv.setVisibility(View.GONE);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_pick;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Places.initialize(LocationPickActivity.this, getString(R.string.google_map_key), Locale.getDefault());
        source.setText(RIDE_REQUEST.containsKey(SRC_ADD) ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get(SRC_ADD)).toString()) ? "" : String.valueOf(RIDE_REQUEST.get(SRC_ADD)) : "");
        if (source.getText().toString().isEmpty()) mIsGOTGPSADDRESSWHENLOAD = 0;

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBottomSheetBehavior = BottomSheetBehavior.from(locationBsLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        locationsRv.setLayoutManager(mLinearLayoutManager);
        setPlacesAdapter();
        source.addTextChangedListener(filterTextWatcher);
        destination.addTextChangedListener(filterTextWatcher);
        stop.addTextChangedListener(filterTextWatcher);

        source.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectedEditText = source;
                source.setCursorVisible(true);
                if (RIDE_REQUEST.containsKey(SRC_ADD)) {
                    setCameraPosition(new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG)), mGoogleMap);
                }
            }
            changeMarkerPin(SRC_ADD);
        });

        destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectedEditText = destination;
                destination.setCursorVisible(true);
                if (RIDE_REQUEST.containsKey(DEST_ADD)) {
                    setCameraPosition(new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG)), mGoogleMap);
                }
            }

            if (stopLayout.getVisibility() == View.VISIBLE)
                changeMarkerPin(STOP_ADD);
            else
                changeMarkerPin(DEST_ADD);
        });

        stop.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectedEditText = stop;
                stop.setCursorVisible(true);
                if (RIDE_REQUEST.containsKey(STOP_ADD)) {
                    setCameraPosition(new LatLng((Double) RIDE_REQUEST.get(STOP_LAT), (Double) RIDE_REQUEST.get(STOP_LANG)), mGoogleMap);
                }
            }
            isStop = true;
            changeMarkerPin(DEST_ADD);
        });
        addStop.setVisibility(View.VISIBLE);

        destination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            }
            return false;
        });

        stop.setOnEditorActionListener((v, actionId, event) -> {
            if (stop.getVisibility() == View.GONE)
                return false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            }
            return false;
        });

        destination.setText(RIDE_REQUEST.containsKey(DEST_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get(DEST_ADD)).toString())
                ? ""
                : String.valueOf(RIDE_REQUEST.get(DEST_ADD))
                : "");

        stop.setText(RIDE_REQUEST.containsKey(STOP_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get(STOP_ADD)).toString())
                ? ""
                : String.valueOf(RIDE_REQUEST.get(STOP_ADD))
                : "");

        if (RIDE_REQUEST.containsKey(STOP_ADD)) {
            stopLayout.setVisibility(View.VISIBLE);
            addStop.setVisibility(View.GONE);
        } else {
            stopLayout.setVisibility(View.GONE);
            addStop.setVisibility(View.VISIBLE);
        }


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            actionName = bundle.getString("actionName", Constants.LocationActions.SELECT_SOURCE);

            if (!TextUtils.isEmpty(actionName) && actionName.equalsIgnoreCase(Constants.LocationActions.SELECT_SOURCE)) {
                destination.setCursorVisible(false);
                source.setCursorVisible(true);
                source.requestFocus();
                selectedEditText = source;
                changeMarkerPin(SRC_ADD);
            } else if (!TextUtils.isEmpty(actionName) && actionName.equalsIgnoreCase(Constants.LocationActions.SELECT_DESTINATION)) {
                source.setCursorVisible(false);
                destination.setCursorVisible(true);
                destination.setText("");
                destination.requestFocus();
                selectedEditText = destination;
                if (stopLayout.getVisibility() == View.VISIBLE)
                    changeMarkerPin(STOP_ADD);
                else
                    changeMarkerPin(DEST_ADD);

            } else if (!TextUtils.isEmpty(actionName) && (actionName.equals(Constants.LocationActions.SELECT_STOP))) {
                stopLayout.setVisibility(View.VISIBLE);
                source.setCursorVisible(false);
                destination.setCursorVisible(false);
                stop.setCursorVisible(true);
                stop.setText("");
                source.requestFocus();
                selectedEditText = stop;
                changeMarkerPin(DEST_ADD);
            } else if (!TextUtils.isEmpty(actionName) && actionName.equals(Constants.LocationActions.CHANGE_DESTINATION)) {
                llSource.setVisibility(View.GONE);
                source.setHint(getString(R.string.select_location));
                selectedEditText = destination;
                changeMarkerPin(DEST_ADD);
            } else if (!TextUtils.isEmpty(actionName) && (actionName.equals(Constants.LocationActions.SELECT_HOME) || actionName.equals(Constants.LocationActions.SELECT_WORK))) {
                destinationLayout.setVisibility(View.GONE);
                selectedEditText = destination;
                source.setText("");
                source.setHint(getString(R.string.select_location));
                changeMarkerPin(DEST_ADD);
            } else {
                destinationLayout.setVisibility(View.VISIBLE);
                llSource.setVisibility(View.VISIBLE);
                source.setHint(getString(R.string.pickup_location));
                selectedEditText = source;
                changeMarkerPin(SRC_ADD);
            }
        }
        presenter.address();
    }

    private void setPlacesAdapter() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, BOUNDS);
        locationsRv.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.setClickListener(this);
    }

    @Override
    public void onPlaceClick(Place place) {
        if (mAutoCompleteAdapter.getItemCount() == 0) return;

        LatLng latLng = place.getLatLng();
        isLocationRvClick = true;
        isSettingLocationClick = true;
        setLocationText(String.valueOf(place.getAddress()), latLng, isLocationRvClick, isSettingLocationClick);
        setCameraPosition(latLng, mGoogleMap);
    }

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick,
                                 boolean isSettingLocationClick) {
        if (address != null && latLng != null) {
            isEditable = false;
            selectedEditText.setText(address);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                s_address = address;
                s_latitude = latLng.latitude;
                s_longitude = latLng.longitude;
                RIDE_REQUEST.put(SRC_ADD, address);
                RIDE_REQUEST.put(SRC_LAT, latLng.latitude);
                RIDE_REQUEST.put(SRC_LONG, latLng.longitude);
            }

            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.put(DEST_ADD, address);
                RIDE_REQUEST.put(DEST_LAT, latLng.latitude);
                RIDE_REQUEST.put(DEST_LONG, latLng.longitude);

             /*   if (isLocationRvClick && !isStop) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }*/
            }

            if (selectedEditText.getTag().equals("stop")) {
                RIDE_REQUEST.put(STOP_ADD, address);
                RIDE_REQUEST.put(STOP_LAT, latLng.latitude);
                RIDE_REQUEST.put(STOP_LANG, latLng.longitude);
                RIDE_REQUEST.put(WAY_LOCATIONS, new JSONArray().put(getAddressJson(latLng)));

                if (isLocationRvClick && isStop) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            }

        } else {
            isEditable = false;
            selectedEditText.setText("");
            locationsRv.setVisibility(View.GONE);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                RIDE_REQUEST.remove(SRC_ADD);
                RIDE_REQUEST.remove(SRC_LAT);
                RIDE_REQUEST.remove(SRC_LONG);
            }
            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.remove(DEST_ADD);
                RIDE_REQUEST.remove(DEST_LAT);
                RIDE_REQUEST.remove(DEST_LONG);
            }
            if (selectedEditText.getTag().equals("stop")) {
                RIDE_REQUEST.remove(STOP_ADD);
                RIDE_REQUEST.remove(STOP_LAT);
                RIDE_REQUEST.remove(STOP_LANG);
                RIDE_REQUEST.remove(WAY_LOCATIONS);
            }
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locationsRv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    private void displayCurrentAddress(LatLng target) {
        if (mGoogleMap == null) return;

        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                return;

            source.setText(getAddress(target));
            mIsGOTGPSADDRESSWHENLOAD = 1;
            hideKeyboard();
        } else getLocationPermission();
    }

    @OnClick({R.id.source, R.id.destination, R.id.reset_source, R.id.reset_destination, R.id.home_address_layout, R.id.work_address_layout, R.id.reset_stop, R.id.add_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                break;
            case R.id.destination:
                break;
            case R.id.reset_source:
                selectedEditText = source;
                source.requestFocus();
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                changeMarkerPin(SRC_ADD);
                break;
            case R.id.reset_destination:
                destination.requestFocus();
                selectedEditText = destination;
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                if (stopLayout.getVisibility() == View.VISIBLE)
                    changeMarkerPin(STOP_ADD);
                else
                    changeMarkerPin(DEST_ADD);
                break;
            case R.id.reset_stop:
                stop.requestFocus();
                selectedEditText = stop;
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                changeMarkerPin(DEST_ADD);
                break;
            case R.id.home_address_layout:
                if (home != null)
                    setLocationText(home.getAddress(),
                            new LatLng(home.getLatitude(), home.getLongitude()),
                            isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.work_address_layout:
                if (work != null)
                    setLocationText(work.getAddress(),
                            new LatLng(work.getLatitude(), work.getLongitude()),
                            isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.add_stop:
                stopLayout.setVisibility(View.VISIBLE);
                addStop.setVisibility(View.GONE);
                changeMarkerPin(STOP_ADD);
                break;
        }
    }

    @Override
    public void onCameraIdle() {
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                hideKeyboard();
                setLocationText(address, cameraPosition.target, isLocationRvClick, isSettingLocationClick);
            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        System.out.println("LocationPickActivity.onCameraMove");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            //      Google map custom style...
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.mGoogleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        BOUNDS = mGoogleMap.getCameraPosition().target;
        setPlacesAdapter();
        if (mIsGOTGPSADDRESSWHENLOAD == 0) {
            displayCurrentAddress(mGoogleMap.getCameraPosition().target);
        }
    }

    void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
    }

    private void changeMarkerPin(String type) {
        switch (type) {
            case SRC_ADD:
                markerPin.setImageResource(R.drawable.start);
                break;
            case DEST_ADD:
                markerPin.setImageResource(R.drawable.last);
                break;
            case STOP_ADD:
                markerPin.setImageResource(R.drawable.stop);
                break;
        }

    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
            }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_pick_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (!TextUtils.isEmpty(actionName) && actionName.equals(Constants.LocationActions.SELECT_HOME) || actionName.equals(Constants.LocationActions.SELECT_WORK)) {
                    Intent intent = new Intent();
                    intent.putExtra(SRC_ADD, s_address);
                    intent.putExtra(SRC_LAT, s_latitude);
                    intent.putExtra(SRC_LONG, s_longitude);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
                return true;
            //            case android.R.id.home:
            //                Toast.makeText(getApplicationContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
            //                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(AddressResponse address) {
        if (address.getHome().isEmpty()) homeAddressLayout.setVisibility(View.GONE);
        else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(home.getAddress());
            homeAddressLayout.setVisibility(View.VISIBLE);
        }

        if (address.getWork().isEmpty()) workAddressLayout.setVisibility(View.GONE);
        else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(work.getAddress());
            workAddressLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    public void setCameraPosition(LatLng latLong, GoogleMap mMap) {
        if (mLocationPermissionGranted) {
            CameraPosition cameraPosition = new CameraPosition.Builder().zoom(14).target(latLong).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

}
