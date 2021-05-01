package com.sourav.dogesan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.company.scrapper.data.AnimeSlide;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sourav.dogesan.utils.AnimeAsyncTaskLoader;
import com.sourav.dogesan.utils.BlurTransformation;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.EpisodesRecycleAdapter;
import com.sourav.dogesan.utils.FirebaseContract;
import com.sourav.dogesan.utils.MyAnimeList;


import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.xml.transform.sax.SAXResult;

import static com.google.android.exoplayer2.Player.*;

public class WatchAnime extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>>,
        EpisodesRecycleAdapter.OnClickedEpisodes {
    public final static String EXTRA_KEY_ANIME = "myanime";
    public static final int WATCH_ANIME = 13;
    private boolean bookmarkState = true;
    private int position = -1;
    private static String animeUid;
    private EpisodesRecycleAdapter episodesRecycleAdapter;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private String path;
    private String video_path;
    private boolean playWhenReady = true;
    private final PlaybackStateListener listner = new PlaybackStateListener();
    private ImageView fullscreen;
    private String pagePath;
    private DatabaseReference bookmark;
    // private DatabaseReference bookmarkCheakReferance ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_anime);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        bookmark = FirebaseDatabase.getInstance()
                .getReference(FirebaseContract.USER)
                .child(DogeViewModel.USER_UID)
                .child(FirebaseContract.MY_LIST);

        pagePath = getIntent().getStringExtra(EXTRA_KEY_ANIME);
        setBookmarkState();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        RecyclerView recyclerView = findViewById(R.id.anime_recycle_view);
        episodesRecycleAdapter = new EpisodesRecycleAdapter();
        LinearLayoutManager L = new LinearLayoutManager(this);
        L.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(L);
        recyclerView.setAdapter(episodesRecycleAdapter);
        episodesRecycleAdapter.setOnEpisodeClickListner(this);
        playerView = findViewById(R.id.player_view);
        fullscreen = findViewById(R.id.player_fullscreen);

        Objects.requireNonNull(getSupportActionBar()).hide();
        fullscreen.setOnClickListener(v -> {
            if (DogeViewModel.isFullScreen()) {
                DogeViewModel.setFullScreen(false);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                DogeViewModel.setFullScreen(true);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
        getSupportLoaderManager().initLoader(WATCH_ANIME, null, this);
        if (savedInstanceState == null) {
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        } else {

           // simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
         //   playerView.setPlayer(simpleExoPlayer);
            playerView.setPlayer(DogeViewModel.getPlayer());
            String s = DogeViewModel.uri;
         //   MediaItem mediaItem = MediaItem.fromUri(s);
          //  simpleExoPlayer.setMediaItem(mediaItem);
         //   simpleExoPlayer.setPlayWhenReady(playWhenReady);

           // simpleExoPlayer.seekTo(DogeViewModel.currentwindo, DogeViewModel.currentposition);

           // simpleExoPlayer.addListener(listner);
          // simpleExoPlayer.prepare();

            boolean state = DogeViewModel.isFullScreen();
            ConstraintLayout frame = findViewById(R.id.anime_half_part);
            frame.setVisibility(View.GONE);

            if (state) {
                //for full screen
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
                params.height = FrameLayout.LayoutParams.MATCH_PARENT;

                playerView.setLayoutParams(params);
                hideSystemUi();

            } else {
                //for potrate mode
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();

                final float scale = getResources().getDisplayMetrics().density;
                params.height = (int) (300 * scale + 0.5f);

                playerView.setLayoutParams(params);
                showSystemUi();
            }
        }
    }

    private void setBookmarkState() {
        List<MyAnimeList> buffer = DogeViewModel.getMyAnimeList();
        ImageView imageView = findViewById(R.id.bookmark);
        int size = buffer.size();
        for (int i = 0; i < size; i++) {
            if (buffer.get(i).getPath().equals(this.pagePath)) {
                bookmarkState = false;
                animeUid = buffer.get(i).getUID();
                position = i;
                break;
            }
        }
        if (bookmarkState) {
            imageView.setImageResource(R.drawable.bookmark_add_24);

        } else {
            imageView.setImageResource(R.drawable.bookmark_added_24);

        }
    }

    @Override
    public void onBackPressed() {
        if(DogeViewModel.isFullScreen()){
            Toast.makeText(getApplicationContext(),"First Exit FullScreen Mode",Toast.LENGTH_SHORT).show();
        }else {
            super.onBackPressed();
            if(playerView!=null){
                if(playerView.getPlayer()!=null){
                    playerView.getPlayer().release();
                    playerView.setPlayer(null);
                    DogeViewModel.setPlayer(null);
                }
            }
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (playerView != null){
          if ( playerView.getPlayer() !=null){
              DogeViewModel.setPlayer(playerView.getPlayer());
          }
        }

//        if (playerView.getPlayer() != null) {
//            DogeViewModel.currentwindo = playerView.getPlayer().getCurrentWindowIndex();
//            DogeViewModel.currentposition = (int) playerView.getPlayer().getCurrentPosition();
//            playerView.getPlayer().release();
//            simpleExoPlayer = null;
//            playerView.setPlayer(null);
//        }
    }

    private void hideSystemUi() {
        int Flag = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(Flag);
        //Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void showSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        // Objects.requireNonNull(getSupportActionBar()).show();
    }

    //Async Loader callbacks
    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new AnimeAsyncTaskLoader(this, id, getIntent().getStringExtra(EXTRA_KEY_ANIME));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        //   com.company.scrapper.data.AnimeCard animeCard = DogeViewModel.getAnimeCard();

        if (DogeViewModel.getAnimeCard() != null && !DogeViewModel.isFullScreen()) {
            ImageView image = findViewById(R.id.anime_image);
            ImageView blurimage = findViewById(R.id.anime_iamge_blur);

            Glide.with(this).load(DogeViewModel.getAnimeCard().getImg_path())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(getApplicationContext())))
                    .into(blurimage);

            Glide.with(this).load(DogeViewModel.getAnimeCard().getImg_path()).into(image);

            episodesRecycleAdapter.updateData(DogeViewModel.getAnimeCard().getEpisodes());
            TextView textView = findViewById(R.id.anime_title);
            textView.setText(DogeViewModel.getAnimeCard().getTitle());
            textView = findViewById(R.id.anime_description);
            textView.setText(DogeViewModel.getAnimeCard().getDescription());

            ImageView bookmarkImage = findViewById(R.id.bookmark);
            // on bookmark click listener
            bookmarkImage.setOnClickListener(v -> {
                if (bookmarkState) {
                    // adding to bookmark
                    MyAnimeList an = new MyAnimeList(DogeViewModel.getAnimeCard().getTitle(),
                            pagePath,
                            DogeViewModel.getAnimeCard().getImg_path(),
                            "null");
                    //TODO bookmark thing
                    bookmark.push().setValue(an);
                    Toast.makeText(getApplicationContext(), "bookmarked", Toast.LENGTH_SHORT).show();
                    bookmarkImage.setImageResource(R.drawable.bookmark_added_24);
                    bookmarkState = false;
                } else {
                    // removing from bookmark
                    bookmarkImage.setImageResource(R.drawable.bookmark_add_24);
                    bookmark.child(animeUid).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            DogeViewModel.getMyAnimeList().remove(position);
                            Toast.makeText(getApplicationContext(), "UnbookMarked", Toast.LENGTH_SHORT).show();
                        }
                    });
                    bookmarkState = true;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }

    // TODO when episode get clicked
    @Override
    public void episodeClicked(String path) {
        if (this.path != null) {
            if (this.path.equals(path)) {
                return;
            }
        }
        this.path = path;
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        EpisodeStream stream = new EpisodeStream();
        stream.execute("null");
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
        final float scale = getResources().getDisplayMetrics().density;
        params.height = (int) (300 * scale + 0.5f);
        playerView.setLayoutParams(params);
        ConstraintLayout constraintLayout = findViewById(R.id.anime_half_part);
        constraintLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initlizePlayer(String uri) {

        // TODO I found the bug that causing to reinitialize the player after rotation is here { simpleexoplayer is null after rotation as i thought}
        if (simpleExoPlayer != null) {
            playerView.setPlayer(simpleExoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(uri);
            simpleExoPlayer.setMediaItem(mediaItem);
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
            // simpleExoPlayer.seekTo(currentWindow,playbackPosition);
            simpleExoPlayer.addListener(listner);
            simpleExoPlayer.prepare();
        }
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
                    if (video_path != null) {
                        DogeViewModel.uri = video_path;
                    }
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