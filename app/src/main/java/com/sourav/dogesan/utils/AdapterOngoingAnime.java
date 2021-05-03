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

    private List<com.company.scrapper.data.AnimeSlide> lsit = new ArrayList<>();

    private OnclickItem listner;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anime_home_card_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.company.scrapper.data.AnimeSlide slide = lsit.get(position);

        Glide.with(holder.view).load(slide.getGif_path()).into(holder.anime_image);
        holder.setAnime_title(slide.getTitle());
    }

    public void updateList(List<com.company.scrapper.data.AnimeSlide> s) {
        this.lsit = s;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lsit.size();
    }

    public void addOnItemListner(OnclickItem listner){
        this.listner = listner ;
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        private TextView anime_title;
        ImageView anime_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            itemView.setOnClickListener(this);
            anime_image = itemView.findViewById(R.id.anime_iamge_card_view);
            anime_title = itemView.findViewById(R.id.anime_title_card_view);
        }

        public void setAnime_title(String title) {
            anime_title.setText(title);

        }

        @Override
        public void onClick(View v) {
            assert listner != null;
            listner.Onclick(lsit.get(getAdapterPosition()).getPath());

        }
    }

    public interface OnclickItem {
        void Onclick(String path);
    }
}
