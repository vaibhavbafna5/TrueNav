package com.example.vaibhav.truenav;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private static ArrayList<TrueData> directions = new ArrayList<>();
    private static LatLng origin;
    private static LatLng destination;

    public static PageFragment newInstance(int page, ArrayList<TrueData> directionsData,
                                           LatLng originData, LatLng destinationData) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        directions = directionsData;
        origin = originData;
        destination = destinationData;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //handles the view logic
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        TextView timeInfo = (TextView) view.findViewById(R.id.time_info);
        TextView moneyInfo = (TextView) view.findViewById(R.id.money_info);
        TextView emissionsInfo = (TextView) view.findViewById(R.id.emissions_info);
        ImageView earthFeeling = (ImageView) view.findViewById(R.id.earth_feeling);
        Log.e("HEY LOOK HERE", Integer.toString(mPage));

        //handles the data
        TrueData data = directions.get(mPage);
        String timeText = data.createTimeText();
        String moneyText = data.createCostText();
        String emissionsText = data.createEmissionsText();

        //updates the views
        timeInfo.setText(timeText);
        moneyInfo.setText(moneyText);
        emissionsInfo.setText(emissionsText);

        switch(mPage) {
            case 0:
                earthFeeling.setImageResource(R.drawable.ecstatic_icon);
                break;
            case 1:
                earthFeeling.setImageResource(R.drawable.happy_icon);
                break;
            case 2:
                earthFeeling.setImageResource(R.drawable.dead_icon);
                break;
            default:
                break;
        }

        //launches Google Maps when button is clicked
        Button launchNavButton = (Button) view.findViewById(R.id.launch_navigation_button);
        launchNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String originLat = Double.toString(origin.latitude);
                String originLong = Double.toString(origin.longitude);
                String destLat = Double.toString(destination.latitude);
                String destLong = Double.toString(destination.longitude);
                String uri = "http://maps.google.com/maps?saddr=";
                uri = uri + originLat + "," + originLong + "&daddr=" + destLat + "," + destLong;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                startActivity(intent);
            }
        });

        return view;
    }

}
