package com.example.geotagvideos;

import android.graphics.Bitmap;

public class videoPOJO {
    Bitmap thumbnail;

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public videoPOJO(Bitmap thumbnail){
        this.thumbnail = thumbnail;
    }
}
