package com.sourav.dogesan.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sourav.dogesan.WatchAnime;
import com.sourav.dogesan.utils.AnimeAsyncTaskLoader;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.R;
import com.sourav.dogesan.utils.SearchRecycleAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<String>>,
        SearchRecycleAdapter.OnItemClickedListner {

    public static final int SEARCH_ANIME_LOADER_ID = 12;
    private ProgressBar progressBar ;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText searchTextView;
    private RecyclerView recyclerView;
    private SearchRecycleAdapter searchRecycleAdapter;


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        synchronized public void onTextChanged(CharSequence s, int start, int before, int count) {

            String result = new String(s.toString()).trim();
            searchRecycleAdapter.updateData(DogeViewModel.getSearchedAnime(result));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        progressBar =view.findViewById(R.id.search_fragment_progressbar);
        searchTextView = view.findViewById(R.id.search_box);
        recyclerView = view.findViewById(R.id.recycle_search_view);
        searchTextView.addTextChangedListener(watcher);
        searchRecycleAdapter = new SearchRecycleAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchRecycleAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) {
                    searchTextView.setVisibility(View.VISIBLE);
                } else if (dy > 0) {
                    searchTextView.setVisibility(View.INVISIBLE);

                }
            }
        });
        getLoaderManager().initLoader(SEARCH_ANIME_LOADER_ID, null, this);
        return view;
    }


    // Loader callback methods
    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AnimeAsyncTaskLoader(getContext(), id);
    }

    @Override
    public void onLoadFinished(@NonNull androidx.loader.content.Loader<List<String>> loader, List<String> data) {
        // use if condition if internet is not connected
       // Toast.makeText(getContext(), "onload", Toast.LENGTH_SHORT).show();
        searchRecycleAdapter.updateData(DogeViewModel.getSearchedAnime(searchTextView.getText().toString()));
        searchRecycleAdapter.setOnclickListner(this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull androidx.loader.content.Loader<List<String>> loader) {

    }

    // when Recycle view item get called
    @Override
    public void onItemClicked(String path) {
      //  Toast.makeText(getContext(), path, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), WatchAnime.class);
        intent.putExtra(WatchAnime.EXTRA_KEY_ANIME,path);
        startActivity(intent);
    }
}