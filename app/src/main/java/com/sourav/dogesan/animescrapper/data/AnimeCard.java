package com.company.scrapper.data;
import java.util.List;
import com.company.scrapper.data.Episodes ;

public class AnimeCard {

    List<Episodes> episodes ;
    String title ;
    String description ;
    String img_path ;

    public AnimeCard( String title, String description, String img_path, List<Episodes> episodes) {
        this.episodes = episodes;
        this.title = title;
        this.description = description;
        this.img_path = img_path;
    }

    public List<Episodes> getEpisodes() {
        return episodes;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImg_path() {
        return img_path;
    }
}
