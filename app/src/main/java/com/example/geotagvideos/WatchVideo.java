package com.example.geotagvideos;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class WatchVideo extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    VideoView video;
    String video_title;
    ArrayList<Double> lat,lon,spd;
    ImageView play;
    Handler handler;
    Marker marker;
    boolean isMarkerAdded = false;
    TextView speed;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        video = findViewById(R.id.video);
        play = findViewById(R.id.play_button);
        speed = findViewById(R.id.speed);
        speed.setVisibility(View.GONE);
        video_title = getIntent().getStringExtra("video_title");
        lat = new ArrayList<Double>();
        lon = new ArrayList<Double>();
        spd = new ArrayList<Double>();
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
        setLatlon_string(mediaStorageDir.getPath());
        video.setVideoPath(mediaStorageDir.getPath()+'/'+video_title);
        handler = new Handler();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setVisibility(View.GONE);
                video.start();
                speed.setVisibility(View.VISIBLE);
                handler.postDelayed(showMarker,0);
            }
        });
    }
    public void setLatlon_string(String directory_path) {
        String text_title = video_title.replace("VID","TXT");
        text_title = text_title.replace(".mp4",".txt");
        BufferedReader bufferedReader = null;
        String line,location[];
        try {
            bufferedReader = new BufferedReader(new FileReader(directory_path + '/' + text_title));
            if(!bufferedReader.ready()){
                throw new IOException();
            }
            while ((line = bufferedReader.readLine())!=null){
                double lati,longi,speed;
                location = line.split(",");
                lati=Double.valueOf(location[0].substring(1).trim());
                longi=Double.valueOf(location[1].trim());
                speed=Double.valueOf(location[2].substring(0,location[2].length()-1).trim());
                lat.add(lati);
                lon.add(longi);
                spd.add(speed);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        for(int i = 0;i<lat.size()-1;i++){
            mMap.addPolyline(new PolylineOptions().add(new LatLng(lat.get(i),lon.get(i)),new LatLng(lat.get(i+1),lon.get(i+1))).width(15).color(Color.BLUE).geodesic(true));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat.get(0),lon.get(0)),20));
        // Add a marker in Sydney and move the camera
    }
    Runnable showMarker = new Runnable() {
        @Override
        public void run() {
            if(i==lat.size()){
                handler.removeCallbacks(showMarker);
                return;
            }else{
                if(isMarkerAdded)
                    marker.remove();
                marker=mMap.addMarker(new MarkerOptions().position(new LatLng(lat.get(i),lon.get(i))).icon(BitmapDescriptorFactory.fromResource(R.drawable.userlocation)).flat(true));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat.get(i),lon.get(i)),18));
                speed.setText(String.valueOf(spd.get(i) + "Km/h"));
                i++;
                isMarkerAdded = true;
                WatchVideo.this.handler.postDelayed(WatchVideo.this.showMarker,1000);
            }
        }
    };
}
