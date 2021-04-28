package com.sourav.dogesan.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.sourav.dogesan.R;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.ViewHolder> {

    private List<com.company.scrapper.data.AnimeSlide> list = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        com.company.scrapper.data.AnimeSlide data = list.get(position);

        viewHolder.setAnimeTitle(data.getTitle());
        Glide.with(viewHolder.view).asGif().load(data.getGif_path()).into(viewHolder.imageHolder);

    }
    public void updateData(List<com.company.scrapper.data.AnimeSlide> slides){
        this.list =slides ;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    class ViewHolder extends SliderViewAdapter.ViewHolder {
        View view;
        private TextView animeTitle;
        ImageView imageHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            imageHolder = itemView.findViewById(R.id.slideingImage);
            animeTitle = itemView.findViewById(R.id.anime_title_slideing);
            this.view = itemView;

        }

        public void setAnimeTitle(String title) {
            animeTitle.setText(title);
        }
    }
}
