package com.sourav.dogesan.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sourav.dogesan.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterOngoingAnime extends RecyclerView.Adapter<AdapterOngoingAnime.ViewHolder> {

    List<com.company.scrapper.data.AnimeSlide> lsit = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_home_card_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.company.scrapper.data.AnimeSlide slide = lsit.get(position);

        Glide.with(holder.view).load(slide.getGif_path()).into(holder.anime_image);
        holder.setAnime_title(slide.getTitle());
    }
    public void updateList(List<com.company.scrapper.data.AnimeSlide> s){
        this.lsit = s ;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lsit.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view ;
       private TextView anime_title;
        ImageView anime_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView ;
            anime_image = itemView.findViewById(R.id.anime_iamge_card_view);
            anime_title = itemView.findViewById(R.id.anime_title_card_view);
        }

        public void setAnime_title(String title) {
            anime_title.setText(title);

        }
    }
}
