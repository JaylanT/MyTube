package com.jaylantse.mytube;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
public class VideoSearchFragment extends Fragment {

    private VideoListFragment videoListFrag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_videos, container, false);
        videoListFrag = (VideoListFragment) getChildFragmentManager().findFragmentById(R.id.fragment);

        return view;
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

    private class AsyncYouTubeSearch extends AsyncTask<String, Void, List<VideoEntry>> {

        @Override
        protected List<VideoEntry> doInBackground(String[] params) {
            String query = params[0];

            try {
                return YouTubeSearch.search(query);
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
}
