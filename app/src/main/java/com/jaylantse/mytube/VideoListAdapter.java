package com.jaylantse.mytube;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;

/**
 * Created by Jaylan Tse on 11/29/2015.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private List<SearchResult> videos;
    private Activity mActivity;
    private final ThumbnailListener thumbnailListener;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;

    /**
     * Constructor
     */
    public VideoListAdapter(List<SearchResult> videos, Activity mActivity) {
        this.videos = videos;
        this.mActivity = mActivity;
        thumbnailListener = new ThumbnailListener();
        thumbnailViewToLoaderMap = new HashMap<>();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view, mActivity);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        SearchResult singleVideo = videos.get(position);
        ResourceId rId = singleVideo.getId();

        // Confirm that the result represents a video. Otherwise, the
        // item will not contain a video ID.
        if (rId.getKind().equals("youtube#video")) {
            String title = singleVideo.getSnippet().getTitle();
            holder.videoTitle.setText(title);

            String videoId = rId.getVideoId();
            holder.videoId = videoId;
            holder.videoThumb.setTag(videoId);
            holder.videoThumb.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
        }
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


    public static class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView videoTitle;
        public YouTubeThumbnailView videoThumb;
        public String videoId;
        public Activity mActivity;

        private static final int REQ_START_STANDALONE_PLAYER = 1;

        public VideoViewHolder(View itemView, Activity mActivity) {
            super(itemView);

            this.mActivity = mActivity;

            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            videoThumb = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(mActivity
                        , DeveloperKey.DEVELOPER_KEY, videoId, 0, true, false);
            mActivity.startActivity(intent);
            mActivity.startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
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
