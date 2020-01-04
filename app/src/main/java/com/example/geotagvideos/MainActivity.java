package com.example.geotagvideos;


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
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    static ArrayList<ArrayList<Double>> locations = new ArrayList<ArrayList<Double>>() ;
    static File mediaStorageDir;
    static Context ctx;
    static String time;
    Handler h;
    Button record_video,show_videos;
    ImageView record_video_button;
    MediaRecorder mediaRecorder;
    Camera camera;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    int CameraId;
    Boolean isrecording = false;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    GPSTracker gpsTracker;
    ProgressDialog progressDialog;
    DecimalFormat df;
    ArrayList<String> current_timestamp;
    ArrayList<Double> lati,longi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.dismiss();
        requestPermissions();
        record_video = findViewById(R.id.record_video);
        record_video_button = findViewById(R.id.record_video_button);
        show_videos = findViewById(R.id.show_video);
        surfaceView = findViewById(R.id.video_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        current_timestamp = new ArrayList<>();
        lati = new ArrayList<>();
        longi = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission_group.MICROPHONE)!= PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission_group.MICROPHONE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
        }
        gpsTracker=new GPSTracker(MainActivity.this);
        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        h=new Handler();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ctx=this.getApplicationContext();
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
                        mediaRecorder.start();
                        isrecording = true;
                        record_video_button.setImageResource(R.drawable.stop);
                        h.postDelayed(runlocation,0);
                    }
                }
            }
        });
        record_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GPSTracker(MainActivity.this);
                visiblity();
            }
        });
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
    private static void setCameraDisplayOrientation(Activity activity,
                                                    int cameraId, android.hardware.Camera camera) {
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
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    public static File getOutputMediaFile(int type){
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        time=timeStamp;
        File mediaFile;
        String filename=mediaStorageDir.getPath()+File.separator+"TXT_"+timeStamp+".txt";
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
    }
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
            create_kml KML = new create_kml();
            String kml_file = KML.kml(current_timestamp,lati,longi,"VID_"+timeStamp);
            lati.clear();
            longi.clear();
            current_timestamp.clear();
            String kml_filename = new String("KML_"+timeStamp+".kml");
            File k_file =new File(mediaStorageDir.getPath()+File.separator+kml_filename);
            FileWriter k_filewriter = new FileWriter(k_file);
            BufferedWriter k_out = new BufferedWriter(k_filewriter);
            k_out.write(kml_file);
            k_out.close();
        }catch (Exception e){
            e.printStackTrace();

        }
        locations.clear();
    }
    public Runnable runlocation=new Runnable() {
        @Override
        public void run() {
            Double latitude=0.0;
            Double longitude=0.0;
            if(gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            }
            ArrayList<Double> lo=new ArrayList<Double>();
            lo.add(latitude);
            lati.add(latitude);
            longi.add(longitude);
            lo.add(longitude);
            lo.add(Double.valueOf(df.format(gpsTracker.getSpeed())));
            locations.add(lo);
            current_timestamp.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzzzzz", Locale.US).format(new Date()));
            MainActivity.this.h.postDelayed(MainActivity.this.runlocation,1000);
        }
    };
}
