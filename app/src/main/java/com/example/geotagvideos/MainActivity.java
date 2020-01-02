package com.example.geotagvideos;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Trace;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    static ArrayList<ArrayList<Double>> locations = new ArrayList<ArrayList<Double>>() ;
    static File mediaStorageDir;
    static Context ctx;
    static String time;
    Handler h;
    Button record_video,show_videos;
    ImageView record_video_button, pause_video;
    MediaRecorder mediaRecorder;
    Camera camera;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    int CameraId;
    //GPSTracker gpsTracker;
    Boolean isrecording = false;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    boolean isPaused = false;
    TimerTask timerTask;
    //MapboxMap mapboxMap;
    //MapView current_map;
    //LocationEngine locationEngine;
    //long DEFAULT_INTERVAL_IN_MILLISECONDS = 2000L;
    //long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    //private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);
    /*LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<location> getlocation = new ArrayList<location>();*/
    ProgressDialog progressDialog;
    DecimalFormat df;
    TextView counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox.getInstance(this,"pk.eyJ1Ijoic2h1YmhhbTI2NSIsImEiOiJjazRpcHk4Z3kwb28wM2xtd2YxYXQ4a3JpIn0.TAp7LAnVGYGvkiX7fbzdAw");
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.dismiss();
        requestPermissions();
        record_video = findViewById(R.id.record_video);
        record_video_button = findViewById(R.id.record_video_button);
        show_videos = findViewById(R.id.show_video);
        pause_video = findViewById(R.id.pause_button);
        surfaceView = findViewById(R.id.video_surface);
        counter = findViewById(R.id.counting);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        h=new Handler();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //current_map = findViewById(R.id.current_map);
        //current_map.onCreate(savedInstanceState);
        //current_map.getMapAsync(this);
        ctx=this.getApplicationContext();
        //current_map.setVisibility(View.INVISIBLE);
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        show_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                startActivity(new Intent(MainActivity.this,videoList.class));
            }
        });
        record_video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isrecording) {
                    releaseMediaRecorder();
                    camera.lock();
                    isrecording = false;
                    h.removeCallbacks(runlocation);
                    backToDefault();
                    writeFileOnInternalStorage(time);
                } else {
                    if (prepareVideoRecorder()) {
                        //mediaRecorder.start();
                        isrecording = true;
                        record_video_button.setImageResource(R.drawable.stop);
                        h.postDelayed(runlocation,0);
                        //mapboxMap.setStyle(Style.MAPBOX_STREETS,
                        //new Style.OnStyleLoaded() {
                        //  @Override
                        //public void onStyleLoaded(@NonNull Style style) {
                                        /*final Handler time_interval = new Handler();
                                        time_interval.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                enableLocationComponent(style);
                                                time_interval.postDelayed(this,1000);
                                            }
                                        },1000);*/
                        //enableLocationComponent(style);
                    }

                        /*locationListener = new MyLocationListener();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    Activity#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for Activity#requestPermissions for more details.
                                        return;
                                    }
                                }
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                            }
                        }, 1000);*/

                    //pause_video.setVisibility(View.VISIBLE);
                }
            }

        });
        record_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visiblity();
            }
        });
        /*pause_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPaused){
                    mediaRecorder.start();
                    isPaused = false;
                    pause_video.setImageResource(R.drawable.pause);
                }else{
                    mediaRecorder.stop();
                    isPaused = true;
                    pause_video.setImageResource(R.drawable.play);
                }
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }
    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }
    public void requestPermissions(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    1000);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1001);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1002);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission_group.MICROPHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission_group.MICROPHONE},
                    1004);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1005);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1003);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openFrontFacingCamera();

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = camera.getParameters();
        camera.setParameters(parameters);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    private void openFrontFacingCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int idx = 0; idx < cameraCount; idx++) {
            Camera.getCameraInfo(idx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    camera = Camera.open(idx);
                    CameraId = idx;
                    setCameraDisplayOrientation(MainActivity.this, CameraId, camera);
                    return;
                } catch (RuntimeException ex) {
                    Toast.makeText(MainActivity.this,"In catch",Toast.LENGTH_LONG).show();
                }
            }
        }
        throw new RuntimeException("Couldn't find front-facing camera!");
    }
    public boolean prepareVideoRecorder(){
        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CameraId, CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setOutputFile(getOutputMediaFile(VIDEO).toString());
        mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static void setCameraDisplayOrientation(Activity activity,
                                                    int cameraId, android.hardware.Camera camera) {
        // see http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
        Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    public static File getOutputMediaFile(int type){
        // TODO
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        time=timeStamp;
        File mediaFile;
        String filename=mediaStorageDir.getPath()+File.separator+"TXT_"+timeStamp+".txt";
        //Log.d("filename",filename);
        if (type == IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    public void visiblity(){
        record_video.setVisibility(View.GONE);
        show_videos.setVisibility(View.GONE);
        surfaceView.setVisibility(View.VISIBLE);
        record_video_button.setVisibility(View.VISIBLE);
    }
    public void backToDefault(){
        record_video.setVisibility(View.VISIBLE);
        show_videos.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.GONE);
        record_video_button.setVisibility(View.GONE);
        record_video_button.setImageResource(R.drawable.recordvideo);
        // locationEngine.removeLocationUpdates(callback);
    }

    /*@Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    /* @Override
     public void onMapReady(@NonNull MapboxMap mapboxMap) {
         this.mapboxMap = mapboxMap;
     }
     public void enableLocationComponent(Style loadMapStyle){
         LocationComponent locationComponent = mapboxMap.getLocationComponent();
         LocationComponentActivationOptions locationComponentActivationOptions =
                 LocationComponentActivationOptions.builder(this, loadMapStyle)
                         .useDefaultLocationEngine(false)
                         .build();
         locationComponent.activateLocationComponent(locationComponentActivationOptions);
         locationComponent.setLocationComponentEnabled(true);
         locationComponent.setCameraMode(CameraMode.TRACKING);
         locationComponent.setRenderMode(RenderMode.COMPASS);
         initLocationEngine();
     }
     public void initLocationEngine(){
         locationEngine = LocationEngineProvider.getBestLocationEngine(this);
         LocationEngineRequest request = new LocationEngineRequest.Builder(100)
                 .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                 .build();
         locationEngine.requestLocationUpdates(request, callback, getMainLooper());
         locationEngine.getLastLocation(callback);
     }
     private static class MainActivityLocationCallback
             implements LocationEngineCallback<LocationEngineResult> {
         private final WeakReference<MainActivity> activityWeakReference;
         MainActivityLocationCallback(MainActivity activity) {
             this.activityWeakReference = new WeakReference<>(activity);
         }
         /**
          * The LocationEngineCallback interface's method which fires when the device's location has changed.
          *
          * @param result the LocationEngineResult object which has the last known location within it.
          */
        /*@Override
        public void onSuccess(LocationEngineResult result) {
            MainActivity activity = activityWeakReference.get();
            ArrayList<Double> lat_lon=new ArrayList<Double>();
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
// Create a Toast which displays the new location's coordinates
                lat_lon.add(result.getLastLocation().getLatitude());
                lat_lon.add(result.getLastLocation().getLongitude());
                locations.add(lat_lon);
                Log.d("location", String.valueOf(locations));
                Toast.makeText(activity,String.valueOf(result.getLastLocation().getLatitude()),
                        Toast.LENGTH_SHORT).show();
// Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }
        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        /*@Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        current_map.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        current_map.onStop();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        current_map.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
// Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        current_map.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        current_map.onLowMemory();
    }*/
    public void writeFileOnInternalStorage(String timeStamp){
        try{
            String filename=new String(("TXT_"+timeStamp+".txt"));
            File file =new File(mediaStorageDir.getPath()+File.separator+filename);
            FileWriter filewriter = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(filewriter);
            for(ArrayList<Double> s : locations)
            {
                out.write(s.toString()+"\n");
            }
            out.close();


        }catch (Exception e){
            e.printStackTrace();

        }
    }
    public Runnable runlocation=new Runnable() {
        @Override
        public void run() {
            Double latitude=0.0;
            Double longitude=0.0;
            GPSTracker gpsTracker=new GPSTracker(MainActivity.this);
            if(gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            }
            if(locations.size()==3) {
                counter.setText("1");
                mediaRecorder.start();
            }
            if(locations.size()==2){
                counter.setVisibility(View.VISIBLE);
                counter.setText("2");
            }
            if(locations.size()==1){
                counter.setVisibility(View.VISIBLE);
                counter.setText("3");
            }
            if(locations.size()>3){
                counter.setVisibility(View.GONE);
            }
            ArrayList<Double> lo=new ArrayList<Double>();
            lo.add(latitude);
            lo.add(longitude);
            lo.add(Double.valueOf(df.format(gpsTracker.getSpeed())));
            locations.add(lo);
            MainActivity.this.h.postDelayed(MainActivity.this.runlocation,1000);
        }
    };
}
    /*private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            /*String longitude = "Longitude: " + loc.getLongitude();
            Log.e("longitude", longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.e("latitude", latitude);*/
            /*double longitude=loc.getLongitude();
            double latitude=loc.getLatitude();
            location ob=new location(longitude,latitude);
            getlocation.add(ob);
            Log.d("location",getlocation.toString());
            System.out.println(getlocation.toString());
            /*------- To get city name from coordinates -------- */
            /*String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            editLocation.setText(s);*/


        /*@Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }*/
