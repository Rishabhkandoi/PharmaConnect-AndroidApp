package com.example.shailesh.dashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.util.ArrayList;

/**
 * Created by Harshit Budhraja on 30-03-2018.
 */

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private ArrayList<Article> arts;
    Context context = null;
    private static final String TAG = "NewsAdapter";



    public NewsAdapter(ArrayList<Article> arts, Context context) {
        this.arts = arts;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeed_card_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ImageView thumbimage_iv = holder.thumbimage;
        TextView heading_tv = holder.heading;
        TextView date_tv = holder.date;
        TextView Description_tv = holder.Description;


        heading_tv.setText(Jsoup.parse(arts.get(position).getHeading()).text());
        date_tv.setText(arts.get(position).getDate());
        Picasso.get().load(Uri.parse(arts.get(position).getThumb())).into(thumbimage_iv);
        String description = Jsoup.parse(arts.get(position).getDesc()).text();
        if(description.length() > 250) {
            description = description.substring(0,249);
        }
        description += "....Read more";
        Description_tv.setText(description);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arts.get(position).getLink()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView thumbimage;
            TextView heading;
            TextView date;
            TextView Description;
            CardView cardView;

            public MyViewHolder(View view) {
                super(view);
                this.thumbimage = view.findViewById(R.id.thumb_image);
                this.heading = view.findViewById(R.id.art_heading);
                this.date = view.findViewById(R.id.art_date);
                this.Description = view.findViewById(R.id.art_desc);

                this.cardView = view.findViewById(R.id.cardView);
            }
        }

}
