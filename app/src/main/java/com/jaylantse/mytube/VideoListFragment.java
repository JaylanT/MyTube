package com.jaylantse.mytube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
public class VideoListFragment extends Fragment {

    private RecyclerView videoRecyclerView;
    private VideoListAdapter videoListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_videos_list, container, false);

        videoRecyclerView = (RecyclerView) view.findViewById(R.id.video_list_recycler_view);

        if (savedInstanceState != null) {
            videoListAdapter = savedInstanceState.getParcelable("adapter");
        } else {
            videoListAdapter = new VideoListAdapter(new ArrayList<VideoEntry>(), getContext());
        }
        videoRecyclerView.setAdapter(videoListAdapter);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("adapter", videoListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        videoListAdapter.releaseLoaders();
    }

    public void setVideoListAdapter(List<VideoEntry> videosList) {
        videoListAdapter = new VideoListAdapter(videosList, getContext());
        videoRecyclerView.setAdapter(videoListAdapter);
    }
}
