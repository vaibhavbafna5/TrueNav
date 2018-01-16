package com.example.vaibhav.truenav;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;

import static android.support.design.widget.BottomSheetBehavior.PEEK_HEIGHT_AUTO;

public class InformationFragment extends ViewPagerBottomSheetDialogFragment {

    private BottomSheetBehavior mBehavior;
    private final int SHEET_HEIGHT = 352;
    private Direction direction;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //gets direction data from the main activity
        ArrayList<TrueData> directions = new ArrayList<>();

        //gets origin data
        LatLng origin = new LatLng(
                this.getArguments().getDouble("originLat"),
                this.getArguments().getDouble("originLong"));

        //gets destination data
        LatLng destination = new LatLng(
                this.getArguments().getDouble("destinationLat"),
                this.getArguments().getDouble("destinationLong"));

        //gets walking data
        String walkingDistance = this.getArguments().getString("walkingDistance");
        String walkingDuration = this.getArguments().getString("walkingDuration");
        TrueData walkingData = new TrueData(
                walkingDistance,
                walkingDuration,
                "",
                false,
                TrueData.DirectionType.WALKING);
        directions.add(walkingData);

        //gets transit data
        String transitDistance = this.getArguments().getString("transitDistance");
        String transitDuration = this.getArguments().getString("transitDuration");
        TrueData transitData = new TrueData(
                transitDistance,
                transitDuration,
                "",
                false,
                TrueData.DirectionType.TRANSIT);
        directions.add(transitData);

        //gets driving data
        String drivingDistance = this.getArguments().getString("drivingDistance");
        String drivingDuration = this.getArguments().getString("drivingDuration");
        String drivingTraffic = this.getArguments().getString("drivingTraffic");
        TrueData drivingData = new TrueData(
                drivingDistance,
                drivingDuration,
                drivingTraffic,
                true,
                TrueData.DirectionType.DRIVING);
        directions.add(drivingData);

        //handles view logistics
        View rootView = inflater.inflate(R.layout.dialog_bottom_sheet, container, false);
        Adapter pagerAdapter = new Adapter(getChildFragmentManager(), getContext(), directions,
                                origin, destination);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.bottom_sheet_viewpager);
        pager.setOffscreenPageLimit(2);
        View dummyView = rootView.findViewById(R.id.bottom_sheet_container);


        final float scale = getContext().getResources().getDisplayMetrics().density;
        final int maxHeight = (int) (SHEET_HEIGHT * scale + 0.5f);

        mBehavior = BottomSheetBehavior.from(dummyView);
        mBehavior.setPeekHeight(maxHeight);

        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.bottom_sheet_tabs);
        tabLayout.setupWithViewPager(pager);

        int[] imageResId = {R.drawable.walk_icon, R.drawable.transit_icon, R.drawable.car_icon};
        for (int i = 0; i < imageResId.length; i++) {
            tabLayout.getTabAt(i).setIcon(imageResId[i]);
        }

        return rootView;
    }

}
