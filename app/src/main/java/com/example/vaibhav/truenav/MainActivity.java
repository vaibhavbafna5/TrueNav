package com.example.vaibhav.truenav;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView dummyTextView = (TextView) findViewById(R.id.dummy_textview);
        String serverKey = "AIzaSyAlmHxG5gNxyrDhSGcrYolJA__x17rNlM0";
        LatLng origin = new LatLng(37.7849569, -122.4068855);
        LatLng destination = new LatLng(37.7814432, -122.4460177);
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        String status = direction.getStatus();
                        dummyTextView.setText(status);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });

    }
}
