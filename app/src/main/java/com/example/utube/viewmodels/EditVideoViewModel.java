package com.example.utube.viewmodels;

import android.app.Application;
import android.net.Uri;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.utube.activities.VideoManager;
import com.example.utube.models.Video;

public class EditVideoViewModel extends AndroidViewModel {
    private MutableLiveData<Video> video = new MutableLiveData<>();
    private MutableLiveData<Uri> newVideoUri = new MutableLiveData<>();
    private MutableLiveData<String[]> categories = new MutableLiveData<>();

    public EditVideoViewModel(Application application) {
        super(application);
        loadCategories();
    }

    private void loadCategories() {
        // This could be loaded from a repository in a real app
        String[] categoriesArray = {"Sport", "News", "Cinema", "Gaming"};
        categories.setValue(categoriesArray);
    }

    public void loadVideo(String videoId) {
        Video loadedVideo = VideoManager.getInstance(getApplication()).getVideoMap().get(videoId);
        video.setValue(loadedVideo);
    }

    public void setNewVideoUri(Uri uri) {
        newVideoUri.setValue(uri);
    }

    public void saveChanges(String newTitle, String newCategory) {
        Video currentVideo = video.getValue();
        if (currentVideo != null) {
            currentVideo.setTitle(newTitle);
            currentVideo.setCategory(newCategory);
            if (newVideoUri.getValue() != null) {
                currentVideo.setVideoUrl(newVideoUri.getValue().toString());
            }
            VideoManager.getInstance(getApplication()).updateVideo(currentVideo);
            video.setValue(currentVideo);
        }
    }

    public LiveData<Video> getVideo() {
        return video;
    }

    public LiveData<String[]> getCategories() {
        return categories;
    }
}