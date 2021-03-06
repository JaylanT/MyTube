package com.jaylantse.mytube;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
public class VideoSearchFragment extends Fragment implements VideoListFragment.Paginator {

    private VideoListFragment videoListFrag;
    private YouTubeSearch youTubeSearch;
    private ProgressBar loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        youTubeSearch = YouTubeSearch.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_videos, container, false);
        videoListFrag = (VideoListFragment) getChildFragmentManager().findFragmentById(R.id.fragment);
        loading = (ProgressBar) view.findViewById(R.id.progressBar);

        // Blank search to get hot videos
        if (savedInstanceState == null) {
            search("");
        }
        return view;
    }

    public void search(String query) {
        new AsyncYouTubeSearch().execute(query);
    }

    private void updateVideoList(final ArrayList<VideoEntry> searchResultList) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoListFrag.setVideoListAdapter(searchResultList);
                }
            });
        }
    }

    private void loadNextPage(final ArrayList<VideoEntry> searchResultList) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoListFrag.addVideosToAdapter(searchResultList);
                    loading.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void paginate() {
        loading.setVisibility(View.VISIBLE);
        new AsyncYouTubeLoadNext().execute();
    }

    private class AsyncYouTubeSearch extends AsyncTask<String, Void, ArrayList<VideoEntry>> {

        @Override
        protected ArrayList<VideoEntry> doInBackground(String[] params) {
            String query = params[0];

            try {
                return youTubeSearch.search(query);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<VideoEntry> videoList) {
            if (videoList != null) {
                updateVideoList(videoList);
            }
        }
    }

    private class AsyncYouTubeLoadNext extends AsyncTask<Void, Void, ArrayList<VideoEntry>> {

        @Override
        protected ArrayList<VideoEntry> doInBackground(Void... params) {
            try {
                return youTubeSearch.loadNextPage();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<VideoEntry> videoList) {
            if (videoList != null) {
                loadNextPage(videoList);
            }
        }
    }
}
