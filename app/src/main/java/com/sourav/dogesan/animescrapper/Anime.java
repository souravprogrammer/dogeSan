package com.company.scrapper;

import com.company.scrapper.data.AnimeCard;
import com.company.scrapper.data.AnimeList;
import com.company.scrapper.data.AnimeSlide;
import com.company.scrapper.data.Episodes;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will run on main thread by default
 * Anime is a Singleton class
 */
public class Anime {


    private static Anime mAnime;
    private static final Object LOCk = new Object();

    private static List<AnimeList> Allanime = null;

    private Anime() {
    }

    public static Anime createInstance() {
        synchronized (LOCk) {
            if (mAnime == null) {
                mAnime = new Anime();
            }
            return mAnime;
        }
    }

    public static Anime getAnime() {
        return mAnime;
    }

    public static String getEpisodeStream(String epi) throws IOException {
        Document document = Jsoup.connect(epi).get();
        Elements elements = document.getElementsByClass("player");
        String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;].*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(elements.toString());

        if (matcher.find()) {
            return matcher.group().replace(" ", "%20");
        } else {

            /** Not found stream link*/
            return null;
        }
    }

    public List<AnimeList> searchAnime(String name) {

        List<AnimeList> result = new ArrayList<>();
        //   List<AnimeList> list = getAllAnime();
        if (name.equals("") && Allanime != null) {
            return Allanime;
        }
        if (Allanime != null) {
            for (AnimeList animeName : Allanime) {
                if (animeName.getTitle().toLowerCase().contains(name)) {
                    result.add(new AnimeList(animeName.getTitle(), animeName.getPath()));
                }
            }

        }
//        if (list == null) {
//            return null;
//        }
        return (result.size() > 0) ? result : null;
    }

    public List<AnimeList> getAllAnime() {
        try {
            Connection connection = Jsoup.connect("http://www.anime1.com/content/list/");
            Document document = connection.get();
            Elements element = document.getElementsByClass("anime-list");

            List<AnimeList> list = new ArrayList<>();

            for (Element e : element) {
                for (Element eli : e.getElementsByTag("li")) {

                    list.add(new AnimeList(eli.getElementsByTag("a")
                            .text(), eli.getElementsByTag("a").attr("href")));
                }
            }
            Allanime = list;
            return list;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public AnimeCard getAnime(String specificAnimePage) throws IOException {
        String img, description, title;
        ArrayList<Episodes> episodes_list = new ArrayList<>();
        Document document = Jsoup.connect(specificAnimePage).get();

        /**Extracting a Discription and image path */
        Elements e = document.getElementsByTag("span");
        description = e.get(e.size() - 1).text();
        // image
        Elements im = document.getElementsByClass("anime");
      //  img =
           Elements ee =     im.get(im.size() - 1).getElementsByTag("img");
           img = ee.get(ee.size()-1).attr("src") ;

        title = document.getElementsByClass("blue-main-title").get(1).text();


        // Elements episode_list = document.getElementsByClass("anime-list").get(1).getElementsByTag("a");

        Elements episode_list = (document.getElementsByClass("anime-list").size() > 1) ?
                document.getElementsByClass("anime-list").get(1).getElementsByTag("a") :
                document.getElementsByClass("anime-list").get(0).getElementsByTag("a");


        for (Element episode : episode_list) {
            /** Fetching list of anime episodes */
            Episodes epi = new Episodes(episode.text(), episode.attr("href"));
            episodes_list.add(epi);
        }
        return new AnimeCard(title, description, img, episodes_list);

    }

    public List<AnimeSlide> getOngoingAnime(Integer start , Integer end) throws IOException {

        List<AnimeSlide> list = new ArrayList<>();
        String title, img, path;
        Document document = Jsoup.connect("http://www.anime1.com/ongoing").get();
        Elements elements = document.getElementsByClass("an-box loading");

        int size = elements.size();

        if(size>end){
            size =end ;
        }
        for (int i = start; i < size; i++) {
            /**path of anime */

            path = elements.get(i).getElementsByClass("an-image")
                    .get(0).getElementsByTag("a")
                    .attr("href");
            /** path of img**/

            img = elements.get(i)
                    .getElementsByClass("an-image")
                    .get(0).getElementsByTag("img").attr("src");


            /** title of anime */

            title = elements.get(i).
                    getElementsByClass("an-text")
                    .get(0).getElementsByTag("h2").get(0).text();

            list.add(new AnimeSlide(title, path, img));

        }

        return (list.size() > 0) ? list : null;
    }

    public List<AnimeSlide> Slide() throws IOException {

        List<AnimeSlide> slides = new ArrayList<>();
        String img, title, path;
        String url = "http://www.anime1.com";
        Document document = Jsoup.connect(url).get();

        Elements elements = document.getElementsByClass("anime-banner harrytabber auto");
        Elements link = elements.get(0).getElementsByTag("a");


        int size = link.size();
        for (int i = 0; i < size; i++) {


            if (!link.get(i).hasText()) {

                // save Added slide object prams here
                img = link.get(i).getElementsByTag("img").attr("src");
                title = link.get(i + 1).text();
                path = link.get(i + 1).attr("href");


                slides.add(new AnimeSlide(title, path, img));

            }

        }

        return slides.size() > 0 ? slides : null;
    }


}
