package com.example.geotagvideos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class videoAdapter extends RecyclerView.Adapter<videoAdapter.MyViewHolder> {
    ArrayList<Bitmap> videoPOJOList;
    ArrayList<String> video_title;
    ArrayList<String> duration;
    ArrayList<String> date;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView video_name,video_duration,date;
        CardView video_card;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            video_name = (TextView)itemView.findViewById(R.id.video_title);
            video_duration = (TextView)itemView.findViewById(R.id.video_duration);
            date = (TextView)itemView.findViewById(R.id.recording_date);
            video_card = (CardView)itemView.findViewById(R.id.video_card);

        }
    }
    public videoAdapter(Context context, ArrayList<Bitmap> videoPOJOList, ArrayList<String> video_title, ArrayList<String> duration, ArrayList<String> date){
        this.videoPOJOList = videoPOJOList;
        this.video_title = video_title;
        this.duration = duration;
        this.date = date;
        this.context = context;
    }
    @NonNull
    @Override
    public videoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull videoAdapter.MyViewHolder holder, final int position) {
        Bitmap video = videoPOJOList.get(position);
        final String title = video_title.get(position);
        String time = duration.get(position);
        String date_R = date.get(position);
        holder.thumbnail.setImageBitmap(video);
        holder.video_name.setText(title);
        holder.video_duration.setText(time);
        holder.date.setText(date_R);
        holder.video_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ShowVideo.class);
                intent.putExtra("video_title",video_title.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoPOJOList.size();
    }
}
