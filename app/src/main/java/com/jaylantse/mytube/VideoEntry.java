package com.jaylantse.mytube;

import java.io.Serializable;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
class VideoEntry implements Serializable {

    private final String title;
    private final String videoId;
    private final String publishedAt;
    private int viewCount;

    public VideoEntry(String videoId, String title, String publishedAt) {
        this.videoId = videoId;
        this.title = title;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getViewCount() {
        return viewCount;
    }
}
