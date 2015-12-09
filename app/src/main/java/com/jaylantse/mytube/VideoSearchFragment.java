package com.jaylantse.mytube;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
public class VideoSearchFragment extends Fragment implements VideoListFragment.Searchable {

    private VideoListFragment videoListFrag;
    private YouTubeSearch youTubeSearch;
    private ProgressBar loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_videos, container, false);
        videoListFrag = (VideoListFragment) getChildFragmentManager().findFragmentById(R.id.fragment);
        loading = (ProgressBar) view.findViewById(R.id.progressBar);

        youTubeSearch = new YouTubeSearch();

        // Blank search to get hot videos
        if (savedInstanceState == null) {
            search("");
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstRun", true);
    }

    public void search(String query) {
        new AsyncYouTubeSearch().execute(query);
    }

    private void updateVideoList(final List<VideoEntry> searchResultList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoListFrag.setVideoListAdapter(searchResultList);
            }
        });
    }

    private void loadNextPage(final List<VideoEntry> searchResultList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoListFrag.addVideosToAdapter(searchResultList);
                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void loadMoreVideos() {
        loading.setVisibility(View.VISIBLE);
        new AsyncYouTubeLoadNext().execute();
    }

    private class AsyncYouTubeSearch extends AsyncTask<String, Void, List<VideoEntry>> {

        @Override
        protected List<VideoEntry> doInBackground(String[] params) {
            String query = params[0];

            try {
                return youTubeSearch.search(query);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<VideoEntry> videoList) {
            if (videoList != null) {
                updateVideoList(videoList);
            }
        }
    }

    private class AsyncYouTubeLoadNext extends AsyncTask<Void, Void, List<VideoEntry>> {

        @Override
        protected List<VideoEntry> doInBackground(Void... params) {
            try {
                return youTubeSearch.loadNextPage();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<VideoEntry> videoList) {
            if (videoList != null) {
                loadNextPage(videoList);
            }
        }
    }
}
