package com.jaylantse.mytube;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jaylan Tse on 11/29/2015.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private final ArrayList<VideoEntry> videos;
    private final ThumbnailListener thumbnailListener;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final FavoriteVideos favoriteVideos;

    /**
     * Constructor
     */
    public VideoListAdapter(ArrayList<VideoEntry> videos, Context mContext) {
        this.videos = videos;

        thumbnailListener = new ThumbnailListener();
        thumbnailViewToLoaderMap = new HashMap<>();
        favoriteVideos = FavoriteVideos.getInstance(mContext);
    }

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
        int viewCount = singleVideo.getViewCount();
        String viewCountString = NumberFormat.getInstance().format(viewCount) + " views";
        holder.videoViewCount.setText(viewCountString);

        final String videoId = singleVideo.getVideoId();
        YouTubeThumbnailView videoThumb = holder.videoThumb;
        YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(videoThumb);
        if (loader == null) {
            holder.videoId = videoId;
            videoThumb.setTag(videoId);
            videoThumb.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
        } else {
            videoThumb.setImageResource(R.drawable.loading_thumbnail);
            loader.setVideo(videoId);
        }

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

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
    }

    public ArrayList<VideoEntry> getVideos() {
        return videos;
    }

    public void addVideos(List<VideoEntry> videosList) {
        int lastPosition = getItemCount();
        videos.addAll(videosList);
        notifyItemInserted(lastPosition);
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView videoTitle;
        public final TextView videoPublishedAt;
        public final TextView videoViewCount;
        public final YouTubeThumbnailView videoThumb;
        public final ImageView videoFavorite;
        public String videoId;

        private static final int REQ_START_STANDALONE_PLAYER = 1;

        public VideoViewHolder(View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            videoPublishedAt = (TextView) itemView.findViewById(R.id.published_at);
            videoViewCount = (TextView) itemView.findViewById(R.id.view_count);
            videoThumb = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);
            videoFavorite = (ImageView) itemView.findViewById(R.id.favorite_video);

            videoThumb.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) v.getContext();
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity,
                    DeveloperKey.DEVELOPER_KEY, videoId, 0, true, false);
            try {
                activity.startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } catch (ActivityNotFoundException e){
                Snackbar.make(v.getRootView(), "YouTube is not installed.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            view.setImageResource(R.drawable.loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
            thumbnailViewToLoaderMap.put(view, loader);
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
