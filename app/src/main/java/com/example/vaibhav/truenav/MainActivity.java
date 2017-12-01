package com.example.vaibhav.truenav;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback{

    private GoogleMap googleMap;
    private LatLng origin;
    private LatLng destination;
    private Direction requestedDirection;
    private Route route;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //CREATES THE MAP
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        //FINDS THE START & END LOCATION
        PlaceAutocompleteFragment startFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.starting_location_autocomplete);

        PlaceAutocompleteFragment endFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destination_location_autocomplete);

        startFragment.setHint("Where from?");
        endFragment.setHint("Where to?");


        startFragment.getView().setBackgroundColor(Color.parseColor("#F5F5F5"));
        endFragment.getView().setBackgroundColor(Color.parseColor("#F5F5F5"));


        //SETS THE STARTING & ENDING LOCATION
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

        String serverKey = "AIzaSyAJ7NeKcYQAii0g_Qt6FlzjD4hUAw0SiGo";
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        requestedDirection = direction;
                        drawRoute();
                        testInfo();
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) { }

                });

    }

    public void drawRoute() {

        //animates camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin);
        builder.include(destination);
//        LatLng cameraPosition = builder.build();

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.30); // offset from edges of the map 10% of screen
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, padding));

        //draws the line
        Context drawingContext = getApplicationContext();
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 9));
        googleMap.addMarker(new MarkerOptions().position(origin));
        googleMap.addMarker(new MarkerOptions().position(destination));
        ArrayList<LatLng> directionPositionList = requestedDirection.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
        googleMap.addPolyline(DirectionConverter.createPolyline(drawingContext, directionPositionList, 5, Color.BLUE));

    }

    public void testInfo() {

        TextView dummyTextView = (TextView) findViewById(R.id.dummy_textview);
        route = requestedDirection.getRouteList().get(0);
        Leg leg = route.getLegList().get(0);
        Info info = leg.getDuration();
        dummyTextView.setText(info.getText());

    }


}
