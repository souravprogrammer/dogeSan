package com.sourav.dogesan.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sourav.dogesan.AuthActivity;
import com.sourav.dogesan.R;
import com.sourav.dogesan.WatchAnime;
import com.sourav.dogesan.utils.DogeViewModel;
import com.sourav.dogesan.utils.FirebaseContract;
import com.sourav.dogesan.utils.MyAnimeAdapter;
import com.sourav.dogesan.utils.MyAnimeList;

import java.util.List;
import java.util.Objects;


public class MyAnimeListFragment extends Fragment implements MyAnimeAdapter.OnRemoveClickListner, MyAnimeAdapter.OnItemClickedlistner {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private MyAnimeAdapter adapter;

    public MyAnimeListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.add(DogeViewModel.getMyAnimeList());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_anime_list, container, false);


        // Initializing
        adapter = new MyAnimeAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view_myanimelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.addListnerRemove(this);
        adapter.addOnitemClickListner(this);
        List<MyAnimeList> l = DogeViewModel.getMyAnimeList();
        adapter.add(l);

        recyclerView.setAdapter(adapter);

        return view;
    }

    //on remove clicked

    @Override
    public void onClicked(String key, int position) {
        // Toast.makeText(getContext(), "" +DogeViewModel.getMyAnimeList().get(position).getPath(),Toast.LENGTH_SHORT).show();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(FirebaseContract.USER).child(DogeViewModel.USER_UID);
        DatabaseReference listRef = reference.child(FirebaseContract.MY_LIST);

        listRef.child(key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getContext(), "completly removed", Toast.LENGTH_SHORT).show();
                adapter.remove(position);
            }
        });
    }

    //On item clicked listener
    @Override
    public void onItemClicked(String path) {
        Intent intent = new Intent(getContext(), WatchAnime.class);
        intent.putExtra(WatchAnime.EXTRA_KEY_ANIME, path);
        startActivity(intent);
    }

}