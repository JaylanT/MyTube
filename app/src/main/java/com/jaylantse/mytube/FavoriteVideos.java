package com.jaylantse.mytube;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    private final Context mContext;
    private HashMap<String, VideoEntry> favoritesMap;

    public FavoriteVideos(Context mContext) {
        this.mContext = mContext;

        getFavoritesFromStorage();
    }

    public void addToFavorites(String videoId, VideoEntry videoEntry) {
        favoritesMap.put(videoId, videoEntry);

        try {
            saveFavoriteVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFromFavorites(String videoId) {
        favoritesMap.remove(videoId);

        try {
            saveFavoriteVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean containsVideo(String videoId) {
        return favoritesMap.containsKey(videoId);
    }

    public List<VideoEntry> getFavorites() {
        getFavoritesFromStorage();

        List<VideoEntry> favoriteVideos = new ArrayList<>();
        for(Map.Entry<String, VideoEntry> entry : favoritesMap.entrySet()) {
            favoriteVideos.add(entry.getValue());
        }

        return favoriteVideos;
    }

    private void getFavoritesFromStorage() {
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

    private void saveFavoriteVideos() throws Exception {
        File file = new File(mContext.getDir("data", Context.MODE_PRIVATE), "favorites");
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
        outputStream.writeObject(favoritesMap);
        outputStream.flush();
        outputStream.close();
    }
}
