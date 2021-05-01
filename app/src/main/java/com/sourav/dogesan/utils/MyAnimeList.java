package com.sourav.dogesan.utils;

public class MyAnimeList extends com.company.scrapper.data.AnimeSlide {

    private String UID;

    public MyAnimeList() {
        // default constructor is needed
    }

    public MyAnimeList(String title, String path, String image_path, String uid) {
        super(title, path, image_path);
        this.UID = uid;
    }
    public void setUID(String uid){
        this.UID = uid ;
    }
    public String getUID() {
        return UID;
    }
}
