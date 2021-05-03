package com.sourav.dogesan.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.sourav.dogesan.R;
import com.sourav.dogesan.WatchAnime;
import com.sourav.dogesan.utils.AdapterOngoingAnime;
import com.sourav.dogesan.utils.AnimeAsyncTaskLoader;
import com.sourav.dogesan.utils.AnimePref;
import com.sourav.dogesan.utils.BlurTransformation;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.FirebaseContract;
import com.sourav.dogesan.utils.SliderAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<String>> {

    public static final int HOME_LOADER = 9;
    NestedScrollView homeView;
    ProgressBar homeProgressbar;
    SliderAdapter adapter;
    AdapterOngoingAnime adapterOngoingAnime;
    ImageView upperImage, bluerImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference prefrenceRefrance;
    TextView prfAnimeTitle;
    List<AnimePref> prefs = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
       // getLoaderManager().destroyLoader(HOME_LOADER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bluerImage = view.findViewById(R.id.blure_image);
        upperImage = view.findViewById(R.id.image_upper_blur);
        firebaseDatabase = FirebaseDatabase.getInstance();
        prefrenceRefrance = firebaseDatabase.getReference(FirebaseContract.ANIMEPREF);
        prfAnimeTitle = view.findViewById(R.id.preftitle);
        prefrenceRefrance.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                dataSnapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                    @Override
                    public void accept(DataSnapshot dataSnapsho) {
                        prefs.add(dataSnapsho.getValue(AnimePref.class));

                    }
                });
                setUpPrefranceCard();
              //  Toast.makeText(getContext(),""+dataSnapshot.getValue(),Toast.LENGTH_LONG).show();
            }
        });


        homeProgressbar = view.findViewById(R.id.home_frag_progressbar);
        homeView = view.findViewById(R.id.home_view);
        SliderView sliderView = view.findViewById(R.id.imageSlider);

        adapterOngoingAnime = new AdapterOngoingAnime();
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view_ongoingAnime);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapterOngoingAnime);

        // ImageView imageView = view.findViewById(R.id.blure_image);


//        Glide.with(this).load(R.drawable.punch)
//                .apply(RequestOptihoeons.bitmapTransform(new BlurTransformation(getContext())))
//                .into(imageView);
        adapter = new SliderAdapter();
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();

        getLoaderManager().initLoader(HOME_LOADER, null, this);


        return view;
    }

    private void setUpPrefranceCard() {
        if(prefs.size()>0) {
            Random random = new Random();

            int position = random.nextInt(prefs.size());

            String img = prefs.get(position).getImage_path();
            prfAnimeTitle.setText(prefs.get(position).getTitle());
            Glide.with(this).load(img)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(getContext())))
                    .into(bluerImage);
            Glide.with(this).load(img).into(upperImage);
            bluerImage.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), WatchAnime.class);
                intent.putExtra(WatchAnime.EXTRA_KEY_ANIME, prefs.get(position).getPath());
                startActivity(intent);
            });
        }
    }
    // Async-task loader call back

    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AnimeAsyncTaskLoader(getContext(), id);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        List<com.company.scrapper.data.AnimeSlide> slide = DogeViewModel.getAnimeSlide();

        if (slide != null && DogeViewModel.getOngoingAnime() != null) {
            /** setting up on anime card listener and on anime sliding listener  */
            adapterOngoingAnime.addOnItemListner(path -> {
                Intent intent = new Intent(getContext(), WatchAnime.class);
                intent.putExtra(WatchAnime.EXTRA_KEY_ANIME, path);
                startActivity(intent);
            });
            adapter.addOnSlideClickListner(path -> {
                Intent intent = new Intent(getContext(), WatchAnime.class);
                intent.putExtra(WatchAnime.EXTRA_KEY_ANIME, path);
                startActivity(intent);

            });
            adapter.updateData(slide);
           // setUpPrefranceCard();
           // Toast.makeText(getContext(), "" + DogeViewModel.getOngoingAnime().size(), Toast.LENGTH_LONG).show();
            adapterOngoingAnime.updateList(DogeViewModel.getOngoingAnime());


            homeProgressbar.setVisibility(View.GONE);
            homeView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }
}