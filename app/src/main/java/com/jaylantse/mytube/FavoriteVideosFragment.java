package com.jaylantse.mytube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jaylan Tse on 12/7/2015.
 */
public class FavoriteVideosFragment extends Fragment implements MainActivity.UpdateableFragment {

    private VideoListFragment videoListFrag;
    private FavoriteVideos favoriteVideos;
    private TextView noVideosView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite_videos, container, false);

        favoriteVideos = FavoriteVideos.getInstance(getContext());
        videoListFrag = (VideoListFragment) getChildFragmentManager().findFragmentById(R.id.favorites_fragment);
        noVideosView = (TextView) view.findViewById(R.id.no_favorite_videos);
        update();

        return view;
    }

    @Override
    public void update() {
        if (favoriteVideos.hasChanged()) {
            List<VideoEntry> favoritesList = favoriteVideos.getFavoritesList();
            videoListFrag.setVideoListAdapter(favoritesList);
            if (!favoritesList.isEmpty()) {
                noVideosView.setVisibility(View.GONE);
            } else {
                noVideosView.setVisibility(View.VISIBLE);
            }
        }
    }
}
