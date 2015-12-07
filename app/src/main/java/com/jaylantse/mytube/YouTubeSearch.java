package com.jaylantse.mytube;

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
 * Created by Jaylan Tse on 12/6/2015.
 */
public class YouTubeSearch {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

    public static List<SearchResult> search(String query) throws Exception {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("mytube").build();

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

        SearchListResponse searchResponse = search.execute();

        return searchResponse.getItems();
    }
}
