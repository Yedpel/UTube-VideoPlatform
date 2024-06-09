package com.example.utube.activities;

import com.example.utube.models.Video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoManager {
    private static VideoManager instance;
    private List<Video> videoList = new ArrayList<>();
    private List<Video> filteredVideoList = new ArrayList<>();
    private Map<String, Video> videoMap = new HashMap<>();
    private Map<String, Boolean> likedStateMap = new HashMap<>();
    private Map<String, Integer> likesCountMap = new HashMap<>();

    private VideoManager() {}

    public static VideoManager getInstance() {
        if (instance == null) {
            instance = new VideoManager();
        }
        return instance;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public List<Video> getFilteredVideoList() {
        return filteredVideoList;
    }

    public Map<String, Video> getVideoMap() {
        return videoMap;
    }

    public Map<String, Boolean> getLikedStateMap() {
        return likedStateMap;
    }

    public Map<String, Integer> getLikesCountMap() {
        return likesCountMap;
    }

    public void addVideo(Video video) {
        videoList.add(video);
        filteredVideoList.add(video);
        videoMap.put(video.getId(), video);
    }

    public void removeVideo(String videoId) {
        videoList.removeIf(video -> video.getId().equals(videoId));
        filteredVideoList.removeIf(video -> video.getId().equals(videoId));
        videoMap.remove(videoId);
    }

    public void updateVideo(Video video) {
        videoMap.put(video.getId(), video);
        int index = videoList.indexOf(video);
        if (index != -1) {
            videoList.set(index, video);
        }
        index = filteredVideoList.indexOf(video);
        if (index != -1) {
            filteredVideoList.set(index, video);
        }
    }
}