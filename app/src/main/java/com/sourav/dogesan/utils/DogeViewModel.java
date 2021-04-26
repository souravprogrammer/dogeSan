package com.sourav.dogesan.utils;

import com.company.scrapper.data.AnimeCard;
import com.company.scrapper.data.AnimeList;
import com.company.scrapper.data.AnimeSlide;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;
import java.util.List;

public class DogeViewModel {

    private static List<com.company.scrapper.data.AnimeList> searchedAnime, allAnimeList = null;
    private static com.company.scrapper.data.AnimeCard animeCard = null ;
    private static com.company.scrapper.data.AnimeSlide animeSlide = null;
    private static SimpleExoPlayer simpleExoPlayer ;
    private static boolean FullScreen =false;

    // getters
    public static SimpleExoPlayer getSimpleExoPlayer(){
        return simpleExoPlayer ;
    }

    public static List<AnimeList> getSearchedAnime(String name) {

        List<AnimeList> result = new ArrayList<>();


        if (name.equals("") && allAnimeList != null) {
            return allAnimeList;
        }
        if (allAnimeList != null) {
            for (AnimeList animeName : allAnimeList) {
                if (animeName.getTitle().toLowerCase().contains(name)) {
                    result.add(new AnimeList(animeName.getTitle(), animeName.getPath()));
                }
            }
        }

        return result;
    }

    public static boolean isAllAnimeList() {
        return allAnimeList == null;
    }

    public static AnimeCard getAnimeCard() {
        return animeCard;
    }

    public static AnimeSlide getAnimeSlide() {
        return animeSlide;
    }

    //setters

    public static void setSearchedAnime(List<AnimeList> searchedAnime) {
        DogeViewModel.searchedAnime = searchedAnime;
    }

    public static void setAllAnimeList(List<AnimeList> allAnimeList) {
        DogeViewModel.allAnimeList = allAnimeList;
    }

    public static void setAnimeCard(AnimeCard animeCard) {
        DogeViewModel.animeCard = animeCard;
    }

    public static void setAnimeSlide(AnimeSlide animeSlide) {
        DogeViewModel.animeSlide = animeSlide;
    }

    public static void setSimpleExoPlayer(SimpleExoPlayer simpleExoPlayer){
       DogeViewModel.simpleExoPlayer = simpleExoPlayer ;
    }
    public static boolean isFullScreen(){
        return FullScreen ;
    }
    public static void setFullScreen(boolean b){
        FullScreen = b ;
    }
}
