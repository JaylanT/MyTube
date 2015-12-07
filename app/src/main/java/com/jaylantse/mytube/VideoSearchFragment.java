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

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
public class VideoSearchFragment extends Fragment {

    private VideoListFragment videoListFrag;

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

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
                    new YouTubeSearch().execute(query);

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

    private class YouTubeSearch extends AsyncTask<String, Void, String> {

        private YouTube youtube;

        @Override
        protected String doInBackground(String[] params) {
            String query = params[0];
            search(query);

            return null;
        }

        private void search(String query) {
            try {
                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-search").build();

                YouTube.Search.List search = youtube.search().list("id,snippet");

                String apiKey = DeveloperKey.DEVELOPER_KEY;
                search.setKey(apiKey);
                search.setQ(query);

                // Restrict the search results to only include videos. See:
                // https://developers.google.com/youtube/v3/docs/search/list#type
                search.setType("video");

                // To increase efficiency, only retrieve the fields that the
                // application uses.
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/publishedAt)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();

                updateVideoList(searchResultList);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
