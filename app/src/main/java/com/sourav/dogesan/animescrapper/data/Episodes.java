package com.company.scrapper.data;

public class Episodes {
    private String path;
    private String episodeNumber;

    public Episodes(String episodeNumber, String path) {
        this.episodeNumber = episodeNumber;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }
}
