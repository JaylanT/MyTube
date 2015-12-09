package com.jaylantse.mytube;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaylan Tse on 12/6/2015.
 */
class YouTubeSearch {

    private static YouTube youtube;
    private static YouTube.Search.List search;
    private static SearchListResponse searchResponse;

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

    private static YouTubeSearch instance = null;

    private YouTubeSearch() {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("mytube").build();

        try {
            search = youtube.search().list("id,snippet");
            String apiKey = DeveloperKey.DEVELOPER_KEY;
            search.setKey(apiKey);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/publishedAt),nextPageToken");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YouTubeSearch getInstance() {
        if (instance == null) {
            instance = new YouTubeSearch();
        }
        return instance;
    }

    public List<VideoEntry> search(String query) throws IOException {
        search.setQ(query);
        search.setPageToken("");
        searchResponse = search.execute();
        List<SearchResult> searchResults = searchResponse.getItems();

        List<VideoEntry> videoEntries = new ArrayList<>();
        String videoIds = "";

        for (SearchResult result : searchResults) {
            ResourceId rId = result.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                String title = result.getSnippet().getTitle();
                String publishedAt = result.getSnippet().getPublishedAt().toStringRfc3339();
                String videoId = rId.getVideoId();

                videoEntries.add(new VideoEntry(videoId, title, publishedAt));
                videoIds += videoId + ",";
            }
        }

        List<Integer> viewCounts = getViewCounts(videoIds.substring(0, videoIds.length() - 1));
        for (int i = 0; i < videoEntries.size(); i++) {
            videoEntries.get(i).setViewCount(viewCounts.get(i));
        }

        return videoEntries;
    }

    private List<Integer> getViewCounts(String videoId) throws IOException {
        URL url = new URL("https://www.googleapis.com/youtube/v3/videos?id="
                + videoId + "&key=" +DeveloperKey.DEVELOPER_KEY
                +"&part=statistics&fields=items(statistics(viewCount))");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream is = conn.getInputStream();

        return parseJson(is);
    }

    private List<Integer> parseJson(InputStream stream) {
        Gson gson = new GsonBuilder().create();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        JsonArray arrayOfStats = json.get("items").getAsJsonArray();
        List<Integer> viewCounts = new ArrayList<>();

        for (int i = 0; i < arrayOfStats.size(); i++) {
            viewCounts.add(arrayOfStats.get(i).getAsJsonObject()
                    .get("statistics").getAsJsonObject()
                    .get("viewCount").getAsInt());
        }

        return viewCounts;
    }

    public List<VideoEntry> loadNextPage() throws IOException {
        search.setPageToken(searchResponse.getNextPageToken());
        searchResponse = search.execute();
        List<SearchResult> searchResults = searchResponse.getItems();

        List<VideoEntry> videoEntries = new ArrayList<>();
        String videoIds = "";

        for (SearchResult result : searchResults) {
            ResourceId rId = result.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                String title = result.getSnippet().getTitle();
                String publishedAt = result.getSnippet().getPublishedAt().toStringRfc3339();
                String videoId = rId.getVideoId();

                videoEntries.add(new VideoEntry(videoId, title, publishedAt));
                videoIds += videoId + ",";
            }
        }

        List<Integer> viewCounts = getViewCounts(videoIds.substring(0, videoIds.length() - 1));
        for (int i = 0; i < videoEntries.size(); i++) {
            videoEntries.get(i).setViewCount(viewCounts.get(i));
        }

        return videoEntries;
    }
}
