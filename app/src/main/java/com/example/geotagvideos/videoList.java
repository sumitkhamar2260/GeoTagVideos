package com.example.geotagvideos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class videoList extends AppCompatActivity {
    RecyclerView video_list;
    ArrayList<Bitmap> thumbnail_bitmap;
    ArrayList<String> video_title,duration,date;
    MediaMetadataRetriever mediaMetadataRetriever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        video_list = findViewById(R.id.video_recycler);
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
        thumbnail_bitmap = new ArrayList<Bitmap>();
        video_title = new ArrayList<String>();
        duration = new ArrayList<String>();
        date = new ArrayList<String>();
        GetFiles(mediaStorageDir.getPath());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        video_list.setLayoutManager(mLayoutManager);
        videoAdapter videoA = new videoAdapter(getApplicationContext(),thumbnail_bitmap,video_title,duration,date);
        video_list.setAdapter(videoA);
    }
    public void GetFiles(String directorypath) {
        File f = new File(directorypath);
        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0) {

        } else {
            for (int i = 0; i < files.length; i++)
                if (files[i].getName().endsWith(".mp4")) {
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    Bitmap temporary = ThumbnailUtils.createVideoThumbnail(files[i].getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    thumbnail_bitmap.add(Bitmap.createScaledBitmap(temporary,100,100,false));
                    video_title.add(files[i].getName());
                    Uri uri = Uri.fromFile(new File(files[i].getPath()));
                    Log.d("Path",files[i].getPath().toString());
                    Log.d("URI:",uri.toString());
                    mediaMetadataRetriever.setDataSource(getApplicationContext(), uri);
                    String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    String date_R = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
                    long timeInMillisec = Long.parseLong(time);
                    long dur = timeInMillisec / 1000;
                    long h = dur / 3600;
                    long m = (dur - h * 3600) / 60;
                    long s = dur - (h * 3600 + m * 60);
                    String duration_video = String.format("%dm : %ds",m,s);
                    String formated_date = "";
                    try {
                        Date d = new SimpleDateFormat("yyyyMMdd",Locale.getDefault()).parse(date_R);
                        formated_date = new SimpleDateFormat("MMMM dd','yyyy",Locale.getDefault()).format(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    duration.add(duration_video);
                    date.add(formated_date);
                    mediaMetadataRetriever.release();
                }
        }
    }
}
