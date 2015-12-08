package com.jaylantse.mytube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.api.services.youtube.model.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jaylan Tse on 11/29/2015.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> implements Parcelable {

    private final List<VideoEntry> videos;
    private final ThumbnailListener thumbnailListener;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final FavoriteVideos favoriteVideos;
    private Context mContext;

    /**
     * Constructor
     */
    public VideoListAdapter(List<VideoEntry> videos, Context mContext) {
        this.videos = videos;
        this.mContext = mContext;

        thumbnailListener = new ThumbnailListener();
        thumbnailViewToLoaderMap = new HashMap<>();
        favoriteVideos = new FavoriteVideos(mContext);
    }

    private VideoListAdapter(Parcel in) {
        videos = new ArrayList<>();
        in.readList(videos, SearchResult.class.getClassLoader());

        thumbnailListener = new ThumbnailListener();
        thumbnailViewToLoaderMap = new HashMap<>();
        favoriteVideos = new FavoriteVideos(mContext);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(videos);
    }

    public static final Creator<VideoListAdapter> CREATOR = new Creator<VideoListAdapter>() {
        @Override
        public VideoListAdapter createFromParcel(Parcel in) {
            return new VideoListAdapter(in);
        }

        @Override
        public VideoListAdapter[] newArray(int size) {
            return new VideoListAdapter[size];
        }
    };

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position) {
        final VideoEntry singleVideo = videos.get(position);

        String title = singleVideo.getTitle();
        holder.videoTitle.setText(title);
        String publishedAt = singleVideo.getPublishedAt();
        holder.videoPublishedAt.setText(publishedAt.substring(0, publishedAt.indexOf("T")));

        final String videoId = singleVideo.getVideoId();
        holder.videoId = videoId;
        holder.videoThumb.setTag(videoId);
        holder.videoThumb.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);

        if (favoriteVideos.containsVideo(videoId)) {
            holder.videoFavorite.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            holder.videoFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
        }

        holder.videoFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favoriteVideos.containsVideo(videoId)) {
                    favoriteVideos.addToFavorites(videoId, singleVideo);
                    holder.videoFavorite.setImageResource(R.drawable.ic_star_black_24dp);
                } else {
                    favoriteVideos.removeFromFavorites(videoId);
                    holder.videoFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView videoTitle;
        public final TextView videoPublishedAt;
        public final YouTubeThumbnailView videoThumb;
        public final ImageView videoFavorite;
        public String videoId;

        private static final int REQ_START_STANDALONE_PLAYER = 1;

        public VideoViewHolder(View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            videoPublishedAt = (TextView) itemView.findViewById(R.id.published_at);
            videoThumb = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);
            videoFavorite = (ImageView) itemView.findViewById(R.id.favorite_video);

            videoThumb.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) v.getContext();
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity,
                    DeveloperKey.DEVELOPER_KEY, videoId, 0, true, false);
            activity.startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
        }
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
            view.setImageResource(R.drawable.no_thumbnail);
        }
    }
}
