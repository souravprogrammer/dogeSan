package com.sourav.dogesan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.sourav.dogesan.utils.AnimeAsyncTaskLoader;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.EpisodesRecycleAdapter;


import java.io.IOException;
import java.util.List;

import static com.google.android.exoplayer2.Player.*;


public class WatchAnime extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>>,
        EpisodesRecycleAdapter.OnClickedEpisodes {
    public final static String EXTRA_KEY_ANIME = "myanime";
    public static final int WATCH_ANIME = 13;
    private EpisodesRecycleAdapter episodesRecycleAdapter;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private String path;
    private String video_path;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private final PlaybackStateListener listner = new PlaybackStateListener();
    private ImageView fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_anime);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        getSupportLoaderManager().initLoader(WATCH_ANIME, null, this);
        RecyclerView recyclerView = findViewById(R.id.recycle_view_watch_episodes);
        episodesRecycleAdapter = new EpisodesRecycleAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(episodesRecycleAdapter);
        episodesRecycleAdapter.setOnEpisodeClickListner(this);
        playerView = findViewById(R.id.player_view);
        fullscreen = findViewById(R.id.player_fullscreen);

        fullscreen.setOnClickListener(v -> {
            if (DogeViewModel.isFullScreen()) {
                fullscreen.setImageResource(R.drawable.exit_fullscreen);
               // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                FrameLayout frameLayout = findViewById(R.id.player_frame);
                frameLayout.setVisibility(View.VISIBLE);
                hideSystemUi();
                DogeViewModel.setFullScreen(false);

            } else {
                FrameLayout frameLayout = findViewById(R.id.player_frame);
                frameLayout.setVisibility(View.VISIBLE);
                fullscreen.setImageResource(R.drawable.enter_full_screen);
             //   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                showSystemUi();
                DogeViewModel.setFullScreen(true);

            }
        });

        if (savedInstanceState == null) {
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        } else {
            simpleExoPlayer = DogeViewModel.getSimpleExoPlayer();
            playerView.setPlayer(simpleExoPlayer);
        }

    }

    private void hideSystemUi() {
        int Flag = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(Flag);
        getSupportActionBar().hide();
    }

    private void showSystemUi() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getSupportActionBar().show();
    }

    //Async Loader callbacks
    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new AnimeAsyncTaskLoader(this, id, getIntent().getStringExtra(EXTRA_KEY_ANIME));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {

        com.company.scrapper.data.AnimeCard animeCard = DogeViewModel.getAnimeCard();
        if (animeCard != null) {
            episodesRecycleAdapter.updateData(animeCard.getEpisodes());
            TextView textView = findViewById(R.id.anime_title);
            textView.setText(animeCard.getTitle());
            textView = findViewById(R.id.anime_description);
            textView.setText(animeCard.getDescription());
            ImageView image = findViewById(R.id.anime_image);
            Glide.with(this).load(animeCard.getImg_path()).into(image);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }

    // when episode get clicked
    @Override
    public void episodeClicked(String path) {
        this.path = path;
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        EpisodeStream stream = new EpisodeStream();
        FrameLayout frameLayout = findViewById(R.id.player_frame);
        frameLayout.setVisibility(View.VISIBLE);
        stream.execute("null");


    }

    @Override
    protected void onPause() {
        super.onPause();
     //   releasePlayer();

    }

    private void initlizePlayer(String uri) {

        playerView.setPlayer(simpleExoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.setPlayWhenReady(playWhenReady);
        // simpleExoPlayer.seekTo(currentWindow,playbackPosition);
        simpleExoPlayer.addListener(listner);
        simpleExoPlayer.prepare();
        DogeViewModel.setSimpleExoPlayer(simpleExoPlayer);
    }

    private void releasePlayer() {

        //   simpleExoPlayer.pause();
        simpleExoPlayer.stop();
        simpleExoPlayer = null;
    }

    private class EpisodeStream extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            video_path = s;
            initlizePlayer(video_path);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return com.company.scrapper.Anime.getEpisodeStream(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    private class PlaybackStateListener implements Player.EventListener {
        int server = 0;

        @Override
        public void onPlaybackStateChanged(int playbackState) {

            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    if (server < 12) {
                        initlizePlayer(changeStream(video_path));
                    }
                    Toast.makeText(getApplicationContext(), "IDLE", Toast.LENGTH_SHORT).show();
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    Toast.makeText(getApplicationContext(), "buffering", Toast.LENGTH_SHORT).show();
                    break;
                case ExoPlayer.STATE_READY:
                    Toast.makeText(getApplicationContext(), "Ready", Toast.LENGTH_SHORT).show();

                    break;
                case ExoPlayer.STATE_ENDED:
                    Toast.makeText(getApplicationContext(), "end", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "UNknown", Toast.LENGTH_SHORT).show();

                    break;
            }

        }

        private String changeStream(String stream) {
//            http://st2.anime1.com/[HorribleSubs]%20One%20Piece%20-%2001%20[1080p]_a1.mp4?st=iV4Tl0SSKgMLUMm_MyegMg&e=1619384135
            String s = "st";
            String buffer;
            String c = "" + video_path.charAt(9);
            int x = Integer.valueOf(c);
            x++;
            server = x;
            s = s + c;
            buffer = video_path.replace(s, "st" + x);
            video_path = buffer;
            return buffer;
        }
    }
}