package com.company.scrapper.data;

public class AnimeSlide extends AnimeList {

    private String gif_path ;
    public AnimeSlide(String title, String path, String gif_path) {
        super(title, path);
        this.gif_path = gif_path ;
    }
    public String getGif_path() {
        return gif_path;
    }
}
