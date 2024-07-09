package com.example.utube.data;

import androidx.room.*;
import com.example.utube.models.VideoEntity;
import java.util.List;


@Dao
public interface VideoDao {
    @Insert
    void insert(VideoEntity video);

    @Query("SELECT * FROM videos")
    List<VideoEntity> getAllVideos();

    @Query("SELECT * FROM videos WHERE author = :author")
    List<VideoEntity> getVideosForAuthor(String author);

    @Update
    void updateVideo(VideoEntity video);

    @Delete
    void deleteVideo(VideoEntity video);

    @Query("SELECT * FROM videos WHERE id = :id")
    VideoEntity getVideoById(String id);
}