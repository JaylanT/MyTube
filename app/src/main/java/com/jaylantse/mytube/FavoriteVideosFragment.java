package com.jaylantse.mytube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jaylan Tse on 12/7/2015.
 */
public class FavoriteVideosFragment extends Fragment {

    private VideoListFragment videoListFrag;
    private FavoriteVideos favoriteVideos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite_videos, container, false);

        favoriteVideos = new FavoriteVideos(getContext());
        videoListFrag = (VideoListFragment) getChildFragmentManager().findFragmentById(R.id.favorites_fragment);
        videoListFrag.setVideoListAdapter(favoriteVideos.getFavorites());

        return view;
    }

    public void updateView() {
        videoListFrag.setVideoListAdapter(favoriteVideos.getFavorites());
    }
}
