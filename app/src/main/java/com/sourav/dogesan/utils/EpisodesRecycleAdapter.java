package com.sourav.dogesan.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sourav.dogesan.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EpisodesRecycleAdapter extends RecyclerView.Adapter<EpisodesRecycleAdapter.ViewHolder> {

    private List<com.company.scrapper.data.Episodes> list = new LinkedList<>();

    private OnClickedEpisodes listner;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episodes_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.company.scrapper.data.Episodes data = list.get(position);

        holder.setEpisode_title(data.getEpisodeNumber());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnEpisodeClickListner(OnClickedEpisodes listner) {
        this.listner = listner;
    }

    public void updateData(List<com.company.scrapper.data.Episodes> list) {
        this.list = new LinkedList<>(list);
      //  this.list = list ;
        notifyDataSetChanged();
    }

    public void setOnEpisodeListner(OnClickedEpisodes e){
        listner =e ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView episode_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            episode_title = itemView.findViewById(R.id.episode_item);
            itemView.setOnClickListener(this);
        }

        private void setEpisode_title(String n) {
            episode_title.setText(n);
        }

        @Override
        public void onClick(View v) {
            if (listner != null) {

                    listner.episodeClicked(list.get(getAdapterPosition()).getPath());

            }
        }
    }

   public interface OnClickedEpisodes {
        void episodeClicked(String path) ;
    }
}
