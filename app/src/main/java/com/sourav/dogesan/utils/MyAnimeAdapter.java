package com.sourav.dogesan.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sourav.dogesan.R;

import java.util.ArrayList;
import java.util.List;

public class MyAnimeAdapter extends RecyclerView.Adapter<MyAnimeAdapter.ViewHolder> {

    private List<MyAnimeList> list = new ArrayList<>();
    private OnRemoveClickListner removeClickListner;
    private OnItemClickedlistner itemClickedlistner;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_anime_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyAnimeList card = list.get(position);

        Glide.with(holder.view).load(card.getGif_path()).into(holder.anime_image);

        holder.setTitle(card.getTitle());

    }
    public int getListSize(){
        return list.size();
    }
   public void addOnitemClickListner(OnItemClickedlistner clickedlistner) {
        this.itemClickedlistner = clickedlistner;
    }

    public void update() {
        notifyDataSetChanged();
    }

    public void add(MyAnimeList animeList) {
        list.add(animeList);
        notifyDataSetChanged();
    }

    public void add(List<MyAnimeList> animeLists) {
        this.list = animeLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        private TextView title;
        ImageView anime_image;
        private TextView remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            remove = itemView.findViewById(R.id.my_anime_list_remove);
            title = itemView.findViewById(R.id.my_anime_list_title);
            anime_image = itemView.findViewById(R.id.my_anime_list_image);
            itemView.setOnClickListener(this);
            remove.setOnClickListener(v -> {
                //  Toast.makeText(itemView.getContext()," removed",Toast.LENGTH_SHORT).show();
                if (removeClickListner != null) {
                    removeClickListner.onClicked(list.get(getAdapterPosition()).getUID(), getAdapterPosition());
                }
            });
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        @Override
        public void onClick(View v) {
            if (itemClickedlistner != null) {
                itemClickedlistner.onItemClicked(list.get(getAdapterPosition()).getPath());
            }
        }
    }

    public void addListnerRemove(OnRemoveClickListner listner) {
        this.removeClickListner = listner;
    }

    public void remove(int position) {
        if (list.size() > position) {
            this.list.remove(position);
            notifyDataSetChanged();
        }
    }

    public interface OnRemoveClickListner {
        void onClicked(String key, int position);
    }

    public interface OnItemClickedlistner {
        void onItemClicked(String path);
    }
}
