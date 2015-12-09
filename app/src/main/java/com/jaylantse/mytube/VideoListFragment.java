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
    private LinearLayoutManager mLayoutManager;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

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
        mLayoutManager = new LinearLayoutManager(getContext());
        videoRecyclerView.setLayoutManager(mLayoutManager);

        videoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    visibleItemCount = videoRecyclerView.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        notifyParents();
                        loading = true;
                    }
                }
            }
        });

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
        videoListAdapter.releaseLoaders();
        videoListAdapter = new VideoListAdapter(videosList, getContext());
        videoRecyclerView.setAdapter(videoListAdapter);

        // Reset scroll listener variables
        previousTotal = 0;
        loading = true;
        visibleThreshold = 5;
    }

    public void addVideosToAdapter(List<VideoEntry> videosList) {
        ((VideoListAdapter) videoRecyclerView.getAdapter()).addVideos(videosList);
    }

    private void notifyParents() {
        Fragment parent = getParentFragment();
        if (parent instanceof notifiableFragment) {
            ((notifiableFragment) parent).update();
        }
    }

    interface notifiableFragment {
        void update();
    }
}
