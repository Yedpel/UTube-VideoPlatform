package com.example.utube.data;

import android.app.Application;
import android.util.Log;

import com.example.utube.models.VideoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoRepository {
    private VideoDao videoDao;
    private ExecutorService executorService;

    public VideoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        videoDao = db.videoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(VideoEntity video) {
        executorService.execute(() -> videoDao.insert(video));
    }

    public List<VideoEntity> getAllVideos() {
        try {
            // return executorService.submit(videoDao::getAllVideos).get();
            List<VideoEntity> videos = executorService.submit(videoDao::getAllVideos).get();
            Log.d("VideoRepository", "Fetched " + videos.size() + " videos from database");
            return videos;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VideoRepository", "Error retrieving videos", e);
            return new ArrayList<>();
        }
    }

    public List<VideoEntity> getVideosForAuthor(String author) {
        try {
            List<VideoEntity> videos = executorService.submit(() -> videoDao.getVideosForAuthor(author)).get();
            Log.d("VideoRepository", "Fetched " + videos.size() + " videos for author: " + author); //try-swip
            return videos;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VideoRepository", "Error fetching videos for author: " + author, e); //try-swip
            return new ArrayList<>();
        }
    }

    public void updateVideo(VideoEntity video) {
        executorService.execute(() -> {
            videoDao.updateVideo(video);
            Log.d("VideoRepository", "Updated video in database: " + video.getId() + " with views: " + video.getViews());
        });
    }

    public void deleteVideo(VideoEntity video) {
        executorService.execute(() -> videoDao.deleteVideo(video));
    }

    public VideoEntity getVideoById(String id) {
        try {
            return executorService.submit(() -> videoDao.getVideoById(id)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void incrementViews(String videoId) {
        executorService.execute(() -> {
            videoDao.incrementViews(videoId);
            Log.d("VideoRepository", "Incremented views for video " + videoId);
        });
    }
}