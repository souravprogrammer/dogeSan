package com.company.scrapper.data;

public class AnimeList {
    private String title ;
    private String path ;

    public AnimeList(String title ,String path){
        this.title =title ;
        this.path = path ;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }
}
