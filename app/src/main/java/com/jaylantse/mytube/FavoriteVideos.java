package com.jaylantse.mytube;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jaylan Tse on 12/7/2015.
 */
class FavoriteVideos {

    private static Context mContext;
    private HashMap<String, VideoEntry> favoritesMap;
    private boolean hasChanged;

    private static FavoriteVideos instance = null;

    private FavoriteVideos(Context mContext) {
        FavoriteVideos.mContext = mContext;
        hasChanged = true;
        loadFavoriteVideos();
    }

    public static FavoriteVideos getInstance(final Context mContext) {
        if (instance == null) {
            instance = new FavoriteVideos(mContext);
        }
        return instance;
    }

    public static FavoriteVideos getInstance() {
        return instance;
    }

    public void addToFavorites(String videoId, VideoEntry videoEntry) {
        favoritesMap.put(videoId, videoEntry);
        hasChanged = true;

        saveFavoriteVideos();
    }

    public void removeFromFavorites(String videoId) {
        favoritesMap.remove(videoId);
        hasChanged = true;

        saveFavoriteVideos();
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public boolean containsVideo(String videoId) {
        return favoritesMap.containsKey(videoId);
    }

    public List<VideoEntry> getFavoritesList() {
        hasChanged = false;

        List<VideoEntry> favoriteVideos = new ArrayList<>();
        for(Map.Entry<String, VideoEntry> entry : favoritesMap.entrySet()) {
            favoriteVideos.add(entry.getValue());
        }

        return favoriteVideos;
    }

    private void loadFavoriteVideos() {
        File file = new File(mContext.getDir("data", Context.MODE_PRIVATE), "favorites");

        try {
            ObjectInputStream ois =
                    new ObjectInputStream(new FileInputStream(file));
            favoritesMap = (HashMap) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (favoritesMap == null) {
                favoritesMap = new HashMap<>();
            }
        }
    }

    private void saveFavoriteVideos() {
        File file = new File(mContext.getDir("data", Context.MODE_PRIVATE), "favorites");
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(favoritesMap);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}
