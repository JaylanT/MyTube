package com.jaylantse.mytube;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.services.youtube.model.SearchResult;

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

        EditText searchInput = (EditText) view.findViewById(R.id.video_search_input);
        searchInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // hide keyboard
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    String query = v.getText().toString();
                    new AsyncYouTubeSearch().execute(query);

                    return true;
                }
                return false;
            }
        });

        return view;
    }

    public void updateVideoList(final List<SearchResult> searchResultList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoListFrag.setVideoListAdapter(searchResultList);
            }
        });
    }

    private class AsyncYouTubeSearch extends AsyncTask<String, Void, List<SearchResult>> {

        @Override
        protected List<SearchResult> doInBackground(String[] params) {
            String query = params[0];

            try {
                return YouTubeSearch.search(query);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<SearchResult> videoList) {
            if (videoList != null) {
                updateVideoList(videoList);
            }
        }
    }
}
