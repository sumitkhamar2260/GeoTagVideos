package com.example.geotagvideos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
        ListView lv;
        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);
            lv= (ListView) findViewById(R.id.simpleListView);
            if (ContextCompat.checkSelfPermission(Main2Activity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {


                } else {

                    // No explanation needed, we can request the permission.

                    /*ActivityCompat.requestPermissions(Main2Activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);*/

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.


                }
            } else {

                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "GeoTagged Videos");
                Log.d("path",mediaStorageDir.getPath());
                ArrayList<String> filesinfolder = GetFiles(mediaStorageDir.getPath());
                ArrayAdapter<String> adapter
                        = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        filesinfolder);

                lv.setAdapter(adapter);


            }

        }

    public ArrayList<String> GetFiles(String directorypath) {
        ArrayList<String> Myfiles = new ArrayList<String>();
        File f = new File(directorypath);
        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0) {
            return null;
        } else {
            for (int i = 0; i < files.length; i++)
                Myfiles.add(files[i].getName());
        }
        return Myfiles;
    }
    }