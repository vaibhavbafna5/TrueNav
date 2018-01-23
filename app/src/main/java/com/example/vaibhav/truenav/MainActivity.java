package com.example.vaibhav.truenav;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback{

    private GoogleMap googleMap;
    private LatLng origin;
    private LatLng destination;
    private Direction drivingDirection;
    private Direction walkingDirection;
    private Direction transitDirection;
    private Route route;
    private FloatingActionButton showInfoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //creates the map
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        //finds the start and end location
        PlaceAutocompleteFragment startFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.starting_location_autocomplete);

        PlaceAutocompleteFragment endFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destination_location_autocomplete);

        startFragment.setHint("Where from?");
        endFragment.setHint("Where to?");


        startFragment.getView().setBackgroundColor(Color.parseColor("#F5F5F5"));
        endFragment.getView().setBackgroundColor(Color.parseColor("#F5F5F5"));


        //sets the start and ending location
        PlaceSelectionListener startLocationListener = new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                origin = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MainActivity.this, "we received an error", Toast.LENGTH_SHORT);
            }
        };

        PlaceSelectionListener endLocationListener = new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination = place.getLatLng();
                executeNetworkRequest();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MainActivity.this, "we received an error", Toast.LENGTH_SHORT);
            }
        };

        startFragment.setOnPlaceSelectedListener(startLocationListener);
        endFragment.setOnPlaceSelectedListener(endLocationListener);

        showInfoButton = findViewById(R.id.show_fragment_button);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        LatLng nyc = new LatLng(40.7808, -73.9772);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nyc, 12));

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
    }

    @Override
    public void onDirectionFailure(Throwable t) { }

    public void executeNetworkRequest() {

        long currentTime = getCurrentTime();
        String serverKey = "AIzaSyAJ7NeKcYQAii0g_Qt6FlzjD4hUAw0SiGo";

        //gets driving data
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .departureTime(Long.toString(currentTime))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        drivingDirection = direction;
                        drawRoute();
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) { }

                });

        //gets transit data
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.TRANSIT)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        transitDirection = direction;
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });

        //gets walking data
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        walkingDirection = direction;
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });

    }

    public void drawRoute() {

        //animates camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin);
        builder.include(destination);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.30); // offset from edges of the map 10% of screen
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, padding));

        //draws the line
        Context drawingContext = getApplicationContext();
        googleMap.addMarker(new MarkerOptions().position(origin));
        googleMap.addMarker(new MarkerOptions().position(destination));
        ArrayList<LatLng> directionPositionList = drivingDirection.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
        googleMap.addPolyline(DirectionConverter.createPolyline(drawingContext, directionPositionList, 5, Color.BLUE));

        //creates the bottom sheet dialog with relevant info
        showInfo();

    }

    public void showInfo() {

        //creates data package for each form of transport
        TrueData walkingData = convertData(walkingDirection, false, TrueData.DirectionType.WALKING);
        TrueData transitData = convertData(transitDirection, false, TrueData.DirectionType.TRANSIT);
        TrueData drivingData = convertData(drivingDirection, true, TrueData.DirectionType.DRIVING);

        //send relevant data (origin, destination, transport) to fragment
        Bundle args = new Bundle();

        //sends origin data
        args.putDouble("originLat", origin.latitude);
        args.putDouble("originLong", origin.longitude);

        //sends destination data
        args.putDouble("destinationLat", destination.latitude);
        args.putDouble("destinationLong", destination.longitude);

        //sends walking data
        args.putString("walkingDistance", walkingData.distance);
        args.putString("walkingDuration", walkingData.duration);

        //sends transit data
        args.putString("transitDistance", transitData.distance);
        args.putString("transitDuration", transitData.duration);

        //sends driving data
        args.putString("drivingDistance", drivingData.distance);
        args.putString("drivingDuration", drivingData.duration);
        args.putString("drivingTraffic", drivingData.traffic);

        //creates the fragment
        final InformationFragment informationFragment = new InformationFragment();
        informationFragment.setArguments(args);

        //shows the view and button
        informationFragment.show(getSupportFragmentManager(), informationFragment.getTag());

        //creates button as information fragment is shown
        showInfoButton.setVisibility(View.VISIBLE);
        showInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                informationFragment.show(getSupportFragmentManager(), informationFragment.getTag());
            }
        });

    }

    //used for getting traffic data
    public long getCurrentTime() {

        long now = System.currentTimeMillis();
        now = now / 1000;
        return now;

    }

    public TrueData convertData(Direction direction, Boolean driving, TrueData.DirectionType directionType) {

        //gets object with relevant data
        List<Route> routeList = direction.getRouteList();
        Route route = routeList.get(0);
        List<Leg> legList = route.getLegList();
        Leg leg = legList.get(0);

        //gets distance
        Info info = leg.getDistance();
        String distance = info.getText();

        //gets travel time
        info = leg.getDuration();
        String duration = info.getText();

        String traffic = "";

        if (driving) {
            //gets travel time with traffic
            info = leg.getDurationInTraffic();
            traffic = info.getText();
        }

        TrueData trueData = new TrueData(distance, duration, traffic, driving, directionType);
        return trueData;

    }

}
