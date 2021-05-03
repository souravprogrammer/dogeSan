package com.sourav.dogesan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
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

public class WatchAnime extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>>,
        EpisodesRecycleAdapter.OnClickedEpisodes {
    public final static String EXTRA_KEY_ANIME = "myanime";
    public static final int WATCH_ANIME = 13;
    private boolean bookmarkState = true;
    private int position = -1;
    private static String animeUid;
    private EpisodesRecycleAdapter episodesRecycleAdapter;
    private Player simpleExoPlayer;
    private PlayerView playerView;
    private String path;
    private ProgressBar progressBar;
    private ProgressBar playerProgressBar;
    private NestedScrollView nestedScrollView;
    private String video_path;
    private boolean playWhenReady = true;
    private final PlaybackStateListener listener = new PlaybackStateListener();
    ;
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
        progressBar = findViewById(R.id.watch_anime_progressbar);
        playerProgressBar = findViewById(R.id.player_progressbar);

        nestedScrollView = findViewById(R.id.watch_anime_nestedview);
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


            simpleExoPlayer = DogeViewModel.getPlayer();
            assert simpleExoPlayer != null;
            playerView.setPlayer(simpleExoPlayer);
            String s = DogeViewModel.uri;


            boolean state = DogeViewModel.isFullScreen();
            ConstraintLayout frame = findViewById(R.id.anime_half_part);
            frame.setVisibility(View.GONE);
            TextView player_title = findViewById(R.id.player_title);
            if (DogeViewModel.player_title != null) {
                player_title.setText(DogeViewModel.player_title);
            }
            if (state) {
                //for full screen
                progressBar.setVisibility(View.GONE);
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
        if (DogeViewModel.isFullScreen()) {
            Toast.makeText(getApplicationContext(), "First Exit FullScreen Mode", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
            if (playerView != null) {
                if (playerView.getPlayer() != null) {
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
        if (playerView != null) {
            if (playerView.getPlayer() != null) {
                DogeViewModel.setPlayer(playerView.getPlayer());
            }
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
    }

    private void showSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    //Async Loader callbacks
    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new AnimeAsyncTaskLoader(this, id, getIntent().getStringExtra(EXTRA_KEY_ANIME));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {

        if (DogeViewModel.getAnimeCard() != null && !DogeViewModel.isFullScreen()) {
            progressBar.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
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
                setBookmarkState();
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
                            bookmarkState = true;
                            Toast.makeText(getApplicationContext(), "UnbookMarked", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }

    // TODO when episode get clicked
    @Override
    public void episodeClicked(String path, String episodeNumber) {
        if (this.path != null) {
            if (this.path.equals(path)) {
                return;
            }
        }

        TextView player_title = findViewById(R.id.player_title);
        DogeViewModel.player_title = episodeNumber;
        player_title.setText(episodeNumber);
        this.path = path;
        // Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        EpisodeStream stream = new EpisodeStream();
        stream.execute("null");
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
        final float scale = getResources().getDisplayMetrics().density;
        params.height = (int) (300 * scale + 0.5f);
        playerView.setLayoutParams(params);
        ConstraintLayout constraintLayout = findViewById(R.id.anime_half_part);
        constraintLayout.setVisibility(View.GONE);
        playerProgressBar.setVisibility(View.VISIBLE);
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

            simpleExoPlayer.addListener(listener);
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
                    if (server < 13) {
                        initlizePlayer(changeStream(video_path));
                    }
                    //Toast.makeText(getApplicationContext(), "IDLE", Toast.LENGTH_SHORT).show();
                    playerProgressBar.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    //Toast.makeText(getApplicationContext(), "buffering", Toast.LENGTH_SHORT).show();
                    playerProgressBar.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_READY:
                    playerView.setKeepScreenOn(true);
                    if (video_path != null) {
                        DogeViewModel.uri = video_path;
                    }
                    playerProgressBar.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Ready", Toast.LENGTH_SHORT).show();

                    break;
                case ExoPlayer.STATE_ENDED:
                    playerView.setKeepScreenOn(false);
                    playerProgressBar.setVisibility(View.GONE);
                    // Toast.makeText(getApplicationContext(), "end", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    //Toast.makeText(getApplicationContext(), "UNknown", Toast.LENGTH_SHORT).show();

                    break;
            }

        }

        private String changeStream(String stream) {

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