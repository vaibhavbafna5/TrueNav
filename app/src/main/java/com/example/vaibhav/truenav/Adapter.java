package com.example.vaibhav.truenav;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Adapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] {"Tab1", "Tab2", "Tab3"};
    private Context context;
    private Direction requestedDirection;
    private ArrayList<TrueData> directions = new ArrayList<>();
    private LatLng origin;
    private LatLng destination;

    public Adapter(FragmentManager fm, Context context, ArrayList<TrueData> directions,
                   LatLng origin, LatLng destination) {

        super(fm);
        this.context = context;
        this.directions = directions;
        for (int i = 0; i < 3; i++) {
            directions.get(i).cleanData();
        }
        this.origin = origin;
        this.destination = destination;

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return PageFragment.newInstance(0, directions, origin, destination);
            case 1:
                return PageFragment.newInstance(1, directions, origin, destination);
            case 2:
                return PageFragment.newInstance(2, directions, origin, destination);
            default:
                return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
