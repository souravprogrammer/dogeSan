package com.sourav.dogesan.utils;

public class AnimePref extends com.company.scrapper.data.AnimeList {
    String image_path;

    public AnimePref() {
    }

    public AnimePref(String title, String path, String image_path) {
        super(title, path);
        this.image_path = image_path;
    }

    public String getImage_path() {
        return image_path;
    }
}
