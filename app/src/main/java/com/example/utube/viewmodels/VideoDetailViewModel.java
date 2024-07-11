package com.example.utube.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.utube.activities.VideoManager;
import com.example.utube.models.CommentEntity;
import com.example.utube.models.Video;
import com.example.utube.data.CommentRepository;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailViewModel extends AndroidViewModel {
    private VideoManager videoManager;
    private CommentRepository commentRepository;
    private MutableLiveData<Video> video = new MutableLiveData<>();
    private MutableLiveData<List<Video.Comment>> comments = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLiked = new MutableLiveData<>();

    public VideoDetailViewModel(Application application) {
        super(application);
        videoManager = VideoManager.getInstance(application);
        commentRepository = new CommentRepository(application);
    }

    public void loadVideo(String videoId) {
        Video loadedVideo = videoManager.getVideoById(videoId);
        video.postValue(loadedVideo);
    }

    // Add methods for adding, updating, and deleting comments

    private Video.Comment convertToVideoComment(CommentEntity entity) {
        return new Video.Comment(entity.getId(), entity.getUsername(), entity.getText(),
                entity.getUploadTime(), entity.getLikes(), entity.getProfilePicUrl());
    }

    public void loadComments(String videoId) {
        List<CommentEntity> loadedComments = commentRepository.getCommentsForVideo(videoId);
        List<Video.Comment> convertedComments = new ArrayList<>();
        for (CommentEntity entity : loadedComments) {
            convertedComments.add(convertToVideoComment(entity));
        }
        comments.postValue(convertedComments);
    }

    public void updateLikeStatus(boolean liked) {
        isLiked.postValue(liked);
    }

    public void incrementViews() {
        Video currentVideo = video.getValue();
        if (currentVideo != null) {
            currentVideo.setViews(currentVideo.getViews() + 1);
            videoManager.updateVideo(currentVideo);
            video.postValue(currentVideo);
        }
    }

    // Add getter methods for LiveData objects

    public VideoManager getVideoManager() {
        return videoManager;
    }

    public CommentRepository getCommentRepository() {
        return commentRepository;
    }

    public LiveData<Video> getVideo() {
        return video;
    }

    public LiveData<List<Video.Comment>> getComments() {
        return comments;
    }

    public LiveData<Boolean> getIsLiked() {
        return isLiked;
    }
}