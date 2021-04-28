package com.sourav.dogesan.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.sourav.dogesan.R;
import com.sourav.dogesan.utils.AdapterOngoingAnime;
import com.sourav.dogesan.utils.AnimeAsyncTaskLoader;
import com.sourav.dogesan.utils.BlurTransformation;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.SliderAdapter;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<String>> {

    public static final int HOME_LOADER = 9;
    SliderAdapter adapter;
    AdapterOngoingAnime adapterOngoingAnime;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SliderView sliderView = view.findViewById(R.id.imageSlider);

        adapterOngoingAnime = new AdapterOngoingAnime();
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view_ongoingAnime);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapterOngoingAnime);

        ImageView imageView = view.findViewById(R.id.blure_image);



        Glide.with(this).load(R.drawable.punch)
                .apply(RequestOptions.bitmapTransform( new BlurTransformation(getContext())))
                .into(imageView);
        adapter = new SliderAdapter();
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();

        getLoaderManager().initLoader(HOME_LOADER, null, this);


        return view;
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
            adapter.updateData(slide);

            Toast.makeText(getContext(), "" + DogeViewModel.getOngoingAnime().size(), Toast.LENGTH_LONG).show();

            adapterOngoingAnime.updateList(DogeViewModel.getOngoingAnime());

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }
}