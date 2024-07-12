package com.example.utube.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.utube.activities.VideoManager;
import com.example.utube.models.Video;
import java.util.List;

public class ChannelViewModel extends AndroidViewModel {
    private VideoManager videoManager;
    private MutableLiveData<List<Video>> videos = new MutableLiveData<>();

    public ChannelViewModel(Application application) {
        super(application);
        videoManager = VideoManager.getInstance(application);
    }

    public void loadVideosForAuthor(String authorName) {
        List<Video> authorVideos = videoManager.getVideosForAuthor(authorName);
        videos.postValue(authorVideos);
    }

    public LiveData<List<Video>> getVideos() {
        return videos;
    }

    // TODO: Implement methods for edit and delete user functionality
}