package com.jaylantse.mytube;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Jaylan Tse on 12/5/2015.
 */
class VideoEntry implements Serializable, Parcelable {

    private final String title;
    private final String videoId;
    private final String publishedAt;
    private int viewCount;

    public VideoEntry(String videoId, String title, String publishedAt) {
        this.videoId = videoId;
        this.title = title;
        this.publishedAt = publishedAt;
    }

    private VideoEntry(Parcel in) {
        title = in.readString();
        videoId = in.readString();
        publishedAt = in.readString();
        viewCount = in.readInt();
    }

    public static final Creator<VideoEntry> CREATOR = new Creator<VideoEntry>() {
        @Override
        public VideoEntry createFromParcel(Parcel in) {
            return new VideoEntry(in);
        }

        @Override
        public VideoEntry[] newArray(int size) {
            return new VideoEntry[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(videoId);
        dest.writeString(publishedAt);
        dest.writeInt(viewCount);
    }
}
