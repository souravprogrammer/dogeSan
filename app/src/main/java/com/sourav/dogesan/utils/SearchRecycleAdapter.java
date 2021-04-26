package com.sourav.dogesan.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sourav.dogesan.R;

import java.util.ArrayList;
import java.util.List;

public class SearchRecycleAdapter extends RecyclerView.Adapter<SearchRecycleAdapter.ViewHolder> {

    private List<com.company.scrapper.data.AnimeList> list = new ArrayList<>();
    private OnItemClickedListner listner;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animelist_item
                , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.company.scrapper.data.AnimeList data = list.get(position);

        holder.setAnime_title(data.getTitle());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<com.company.scrapper.data.AnimeList> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView anime_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            anime_title = itemView.findViewById(R.id.anime_title_search_list);
            itemView.setOnClickListener(this);
        }

        public void setAnime_title(String text) {
            anime_title.setText(text);
        }

        @Override
        public void onClick(View v) {
            listner.onItemClicked(list.get(getAdapterPosition()).getPath());
        }
    }

    public void setOnclickListner(OnItemClickedListner listner) {
        this.listner = listner;
    }

    // Recycle view click listner
    public interface OnItemClickedListner {
        void onItemClicked(String path);
    }
}
