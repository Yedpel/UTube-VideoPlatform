package com.example.utube.activities;

import android.app.Application;
import android.util.Log;

import com.example.utube.data.VideoRepository;
import com.example.utube.models.Video;
import com.example.utube.models.VideoEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VideoManager {
    private static VideoManager instance;
    private VideoRepository videoRepository;
    private List<Video> filteredVideoList = new ArrayList<>();
    private Map<String, Boolean> likedStateMap = new HashMap<>();
    private Map<String, Integer> likesCountMap = new HashMap<>();

    private VideoManager(Application application) {
        videoRepository = new VideoRepository(application);
    }

    public static synchronized VideoManager getInstance(Application application) {
        if (instance == null) {
            instance = new VideoManager(application);
        }
        return instance;
    }

    public List<Video> getVideoList() {
        List<VideoEntity> entities = videoRepository.getAllVideos();
        Log.d("VideoManager", "Number of videos in database: " + entities.size());
        return entities.stream().map(this::entityToVideo).collect(Collectors.toList());
    }

    public List<Video> getFilteredVideoList() {
        Log.d("VideoManager", "FilteredVideoList size: " + filteredVideoList.size());
        if (filteredVideoList.isEmpty()) {
            Log.d("VideoManager", "FilteredVideoList is empty, returning all videos");
            return getVideoList();
        }
        return filteredVideoList;
    }

    public Map<String, Video> getVideoMap() {
        Map<String, Video> videoMap = new HashMap<>();
        for (Video video : getVideoList()) {
            videoMap.put(video.getId(), video);
        }
        return videoMap;
    }

    public Map<String, Boolean> getLikedStateMap() {
        return likedStateMap;
    }

    public Map<String, Integer> getLikesCountMap() {
        return likesCountMap;
    }

    public void addVideo(Video video) {
        videoRepository.insert(videoToEntity(video));
        filteredVideoList.add(video);
    }

    public void removeVideo(String videoId) {
        VideoEntity entity = videoRepository.getVideoById(videoId);
        if (entity != null) {
            videoRepository.deleteVideo(entity);
            filteredVideoList.removeIf(v -> v.getId().equals(videoId));
        }
    }

    public void updateVideo(Video video) {
        videoRepository.updateVideo(videoToEntity(video));
        int index = filteredVideoList.indexOf(video);
        if (index != -1) {
            filteredVideoList.set(index, video);
        }
    }

    public void setVideoList(List<Video> videos) {
        for (Video video : videos) {
            videoRepository.insert(videoToEntity(video));
        }
        filteredVideoList.clear();
        filteredVideoList.addAll(videos);
        Log.d("VideoManager", "setVideoList: filteredVideoList size after update: " + filteredVideoList.size());
    }

    public List<Video> getVideosForAuthor(String author) {
        List<VideoEntity> entities = videoRepository.getVideosForAuthor(author);
        return entities.stream().map(this::entityToVideo).collect(Collectors.toList());
    }

    public Video getVideoById(String id) {
        VideoEntity entity = videoRepository.getVideoById(id);
        return entity != null ? entityToVideo(entity) : null;
    }

    private Video entityToVideo(VideoEntity entity) {
        return new Video(entity.id, entity.title, entity.author, entity.views, entity.uploadTime,
                entity.thumbnailUrl, entity.authorProfilePicUrl, entity.videoUrl,
                entity.category, entity.likes);
    }

    private VideoEntity videoToEntity(Video video) {
        return new VideoEntity(video.getId(), video.getTitle(), video.getAuthor(), video.getViews(),
                video.getUploadTime(), video.getThumbnailUrl(), video.getAuthorProfilePicUrl(),
                video.getVideoUrl(), video.getCategory(), video.getLikes());
    }
}