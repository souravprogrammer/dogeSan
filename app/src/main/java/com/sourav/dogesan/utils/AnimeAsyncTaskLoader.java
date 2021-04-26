package com.sourav.dogesan.utils;


import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.company.scrapper.Anime;
import com.sourav.dogesan.WatchAnime;
import com.sourav.dogesan.fragments.SearchFragment;

import java.io.IOException;
import java.util.List;

public class AnimeAsyncTaskLoader extends AsyncTaskLoader<List<String>> {
    private int id;
    private String Extra_data;

    public AnimeAsyncTaskLoader(Context context, int id) {
        super(context);
        this.id = id;
    }

    public AnimeAsyncTaskLoader(Context context, int id, String Extra_data) {
        super(context);
        this.id = id;
        this.Extra_data = Extra_data;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {

        if (id == SearchFragment.SEARCH_ANIME_LOADER_ID) {
            if (DogeViewModel.isAllAnimeList()) {
                DogeViewModel.setAllAnimeList(Anime.createInstance().getAllAnime());
            }
        } else if (id == WatchAnime.WATCH_ANIME) {
                try {
                    com.company.scrapper.data.AnimeCard animeCard =
                            Anime.createInstance().getAnime(Extra_data);
                    DogeViewModel.setAnimeCard(animeCard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
