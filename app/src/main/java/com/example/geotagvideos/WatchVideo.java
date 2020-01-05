package com.example.geotagvideos;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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
    ArrayList<String> time;
    ImageView play;
    Handler handler,seekBar_Handler;
    Marker marker;
    boolean isMarkerAdded = false;
    TextView speed,text_time,text_date,dur,minutes;
    int i=0;
    SeekBar seekBar;
    Button open_earth;
    File mediaStorageDir;
    ImageView play_pause;
    boolean isPaused=true;
    int duration,min,sec,c_dur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Please Try Again",Toast.LENGTH_LONG).show();
        }
        video = findViewById(R.id.video);
        play = findViewById(R.id.play_button);
        speed = findViewById(R.id.speed);
        open_earth = findViewById(R.id.open_earth);
        text_time = findViewById(R.id.time);
        text_date = findViewById(R.id.date);
        speed.setVisibility(View.GONE);
        text_date.setVisibility(View.GONE);
        text_time.setVisibility(View.GONE);
        dur = findViewById(R.id.duration);
        minutes = findViewById(R.id.minutes);
        video_title = getIntent().getStringExtra("video_title");
        lat = new ArrayList<Double>();
        lon = new ArrayList<Double>();
        spd = new ArrayList<Double>();
        time = new ArrayList<String>();
        seekBar = findViewById(R.id.seekBar);
        play_pause = findViewById(R.id.play_pause);
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
        setLatlon_string(mediaStorageDir.getPath());
        video.setVideoPath(mediaStorageDir.getPath()+'/'+video_title);
        handler = new Handler();
        seekBar_Handler = new Handler();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setVisibility(View.GONE);
                video.start();
                play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause_video));
                speed.setVisibility(View.VISIBLE);
                text_date.setVisibility(View.VISIBLE);
                text_time.setVisibility(View.VISIBLE);
                handler.postDelayed(showMarker,0);
                handler.postDelayed(showSeekBar,0);
            }
        });
        open_earth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kml_title = video_title.replace("VID","KML");
                kml_title = kml_title.replace(".mp4",".kml");
                String pathToLocalKmlFile = mediaStorageDir.getPath()+'/'+kml_title;
                openKmlInGoogleEarth(pathToLocalKmlFile);

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getProgress() == video.getDuration()){
                    video.seekTo(video.getDuration());
                    play.setVisibility(View.VISIBLE);
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.playvideo));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar_Handler.removeCallbacks(showSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar_Handler.removeCallbacks(showSeekBar);
                video.seekTo(seekBar.getProgress());
                seekBar_Handler.postDelayed(showSeekBar,100);
                i = video.getCurrentPosition()/1000;
            }
        });
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPaused){
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause_video));
                    isPaused = false;
                    seekBar_Handler.postDelayed(showSeekBar,100);
                    play.setVisibility(View.GONE);
                    video.start();
                    speed.setVisibility(View.VISIBLE);
                    text_time.setVisibility(View.VISIBLE);
                    text_date.setVisibility(View.VISIBLE);
                    handler.postDelayed(showMarker,1000);
                }else{
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.playvideo));
                    isPaused = true;
                    video.pause();
                    i=video.getCurrentPosition()/1000;
                    handler.removeCallbacks(showMarker);
                }
            }
        });
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration()/1000;
                /*int hours = duration / 3600;
                int minutes = (duration / 60) - (hours * 60);
                int seconds = duration - (hours * 3600) - (minutes * 60) ;
                String formatted = String.format("%d:%02d:%02d", hours, minutes, seconds);*/
                dur.setText(String.format("%02d:%02d",(duration / 60) - ((duration/3600) * 60),duration - (duration/3600)*3600 - ((duration / 60) - ((duration/3600) * 60))*60));
            }
        });
    }
    public void setLatlon_string(String directory_path) {
        String text_title = video_title.replace("VID","TXT");
        text_title = text_title.replace(".mp4",".txt");
        BufferedReader bufferedReader;
        String line;
        String[] location;
        try {
            bufferedReader = new BufferedReader(new FileReader(directory_path + '/' + text_title));
            if(!bufferedReader.ready()){
                throw new IOException();
            }
            while ((line = bufferedReader.readLine())!=null){
                double lati,longi,speed;
                String timeStamp;
                location = line.split(",");
                lati=Double.valueOf(location[0].substring(1).trim());
                longi=Double.valueOf(location[1].trim());
                speed=Double.valueOf(location[2].trim());
                timeStamp = location[3].substring(0,location[3].length()-1).trim();
                lat.add(lati);
                lon.add(longi);
                spd.add(speed);
                time.add(timeStamp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(int i = 0;i<lat.size()-1;i++){
            mMap.addPolyline(new PolylineOptions().add(new LatLng(lat.get(i),lon.get(i)),new LatLng(lat.get(i+1),lon.get(i+1))).width(15).color(Color.BLUE).geodesic(true));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat.get(0),lon.get(0)),19));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat.get(0),lon.get(0))).icon(BitmapDescriptorFactory.fromResource(R.drawable.apaddle)).flat(true));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat.get(lat.size()-1),lon.get(lon.size()-1))).icon(BitmapDescriptorFactory.fromResource(R.drawable.bpaddle)).flat(true));
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
                if(i<lat.size()){
                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat.get(i), lon.get(i))).icon(BitmapDescriptorFactory.fromResource(R.drawable.userlocation)).flat(true));
                    speed.setText(spd.get(i) + "Km/h");
                    text_date.setText(time.get(i).substring(0, 19).split("T")[0]);
                    text_time.setText(time.get(i).substring(0, 19).split("T")[1]);
                    i++;
                }
                isMarkerAdded = true;
                WatchVideo.this.handler.postDelayed(WatchVideo.this.showMarker,1000);
            }
        }
    };
    public void openKmlInGoogleEarth(String pathToFileInExternalStorage) {
        try {
            String[] splits = pathToFileInExternalStorage.split("\\.");
            String ext = "";
            if(splits.length >= 2) {
                ext = splits[splits.length-1];
            }
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String mimeType = mime.getMimeTypeFromExtension(ext);
            File file = new File(pathToFileInExternalStorage);
            Uri uri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                uri = Uri.fromFile(file);
            } else {
                uri = FileProvider.getUriForFile(WatchVideo.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);
            }
            final String type = "application/vnd.google-earth.kml+xml";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
        catch(IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(),"In Catch",Toast.LENGTH_LONG).show();
        }
    }
    Runnable showSeekBar = new Runnable() {
        @Override
        public void run() {
            seekBar.setMax(video.getDuration());
            seekBar.setProgress(video.getCurrentPosition());
            c_dur = video.getCurrentPosition()/1000;
            minutes.setText(String.format("%02d:%02d",(c_dur / 60) - ((c_dur/3600) * 60),c_dur - (c_dur/3600)*3600 - ((c_dur / 60) - ((c_dur/3600) * 60))*60));
            seekBar_Handler.postDelayed(showSeekBar,100);
        }
    };
}
