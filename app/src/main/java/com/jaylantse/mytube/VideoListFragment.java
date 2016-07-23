package com.jaylantse.mytube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
public class VideoListFragment extends Fragment {

    private RecyclerView videoRecyclerView;
    private VideoListAdapter videoListAdapter;

    private int previousTotal = 0;
    private int visibleThreshold = 5;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private boolean loading = true;

    private static final String VIDEOS_LIST = "videosList";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_videos_list, container, false);

        if (savedInstanceState != null) {
            ArrayList<VideoEntry> videosList = savedInstanceState.getParcelableArrayList(VIDEOS_LIST);
            videoListAdapter = new VideoListAdapter(videosList, getContext());
        } else {
            videoListAdapter = new VideoListAdapter(new ArrayList<VideoEntry>(), getContext());
        }
        videoRecyclerView = (RecyclerView) view.findViewById(R.id.video_list_recycler_view);
        videoRecyclerView.setAdapter(videoListAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        videoRecyclerView.setLayoutManager(mLayoutManager);

        videoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
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
                        notifyParent();
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

        outState.putParcelableArrayList(VIDEOS_LIST, videoListAdapter.getVideos());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        videoListAdapter.releaseLoaders();
    }

    public void setVideoListAdapter(ArrayList<VideoEntry> videosList) {
        videoListAdapter.releaseLoaders();
        videoListAdapter = new VideoListAdapter(videosList, getContext());
        videoRecyclerView.setAdapter(videoListAdapter);

        // Reset scroll listener variables
        previousTotal = 0;
        loading = true;
        visibleThreshold = 5;
    }

    public void addVideosToAdapter(ArrayList<VideoEntry> videosList) {
        ((VideoListAdapter) videoRecyclerView.getAdapter()).addVideos(videosList);
    }

    private void notifyParent() {
        Fragment parent = getParentFragment();
        if (parent != null) {
            if (parent instanceof Paginator) {
                ((Paginator) parent).paginate();
            }
        }
    }

    interface Paginator {
        void paginate();
    }
}
