package com.example.geotagvideos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowVideo extends AppCompatActivity {
    VideoView video;
    String video_title;
    Uri video_uri;
    ArrayList<Double> lat,lon;
    MapView mapView;
    List<Point> route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoic2h1YmhhbTI2NSIsImEiOiJjazRpcHk4Z3kwb28wM2xtd2YxYXQ4a3JpIn0.TAp7LAnVGYGvkiX7fbzdAw");
        setContentView(R.layout.activity_show_video);
        video = findViewById(R.id.video);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        video_title = getIntent().getStringExtra("video_title");
        lat = new ArrayList<Double>();
        lon = new ArrayList<Double>();
        route = new ArrayList<>();
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
        setLatlon_string(mediaStorageDir.getPath());
        video.setVideoPath(mediaStorageDir.getPath()+'/'+video_title);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(lat.get(0), lon.get(0)))
                                .zoom(13)
                                .build();
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                        initRouteCoordinates();

// Create the LineString from the list of coordinates and then make a GeoJSON
// FeatureCollection so we can add the line to our map as a layer.
                        style.addSource(new GeoJsonSource("line-source",
                                FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
                                        LineString.fromLngLats(route)
                                )})));

// The layer properties for our line. This is where we make the line dotted, set the
// color, etc.
                        style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                                PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                PropertyFactory.lineWidth(5f),
                                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                        ));
                    }
                });
            }
        });

        video.start();
    }
    public Uri getUri(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
        Uri uri = Uri.fromFile(new File(mediaStorageDir.getPath()+video_title));
        return uri;
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
                double lati,longi;
                location = line.split(",");
                lati=Double.valueOf(location[0].substring(1).trim());
                longi=Double.valueOf(location[1].substring(0,location[1].length()-1).trim());
                lat.add(lati);
                lon.add(longi);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initRouteCoordinates(){
        for(int i=0;i<lat.size();i++){
            route.add(Point.fromLngLat(lon.get(i),lat.get(i)));
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}

