package com.example.utube.viewmodels;

import static com.example.utube.activities.MainActivity.PREFS_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.utube.activities.VideoManager;
import com.example.utube.models.Video;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<Video>> videos;
    private VideoManager videoManager;

    public MainViewModel(Application application) {
        super(application);
        videoManager = VideoManager.getInstance(application);
        videos = new MutableLiveData<>();
        //  loadVideos();  // Load videos immediately on ViewModel creation
    }

    public LiveData<List<Video>> getVideos() {
        if (videos.getValue() == null) {
            loadVideos();
        }
        return videos;
    }

    public void loadVideos() {
        List<Video> videoList = videoManager.getVideoList();
        videos.postValue(videoList != null ? videoList : new ArrayList<>());
    }

    public void addVideo(Video video) {
        videoManager.addVideo(video);
        loadVideos();
    }

    public void removeVideo(String videoId) {
        videoManager.removeVideo(videoId);
        loadVideos();
    }

    public void updateVideo(Video video) {
        videoManager.updateVideo(video);
        loadVideos();
    }


    public void clearFilteredList() {
        videoManager.clearFilteredList();
        loadVideos();
    }

    public void setVideoList(List<Video> videoList) {
        videoManager.setVideoList(videoList);
        loadVideos();
    }

    public void saveUserAddedVideos(SharedPreferences sharedPreferences) {
        List<Video> videoList = videoManager.getVideoList();
        for (Video video : videoList) {
            if (video.getId().startsWith("new_")) {
                sharedPreferences.edit().putString(video.getId() + "_videoUrl", video.getVideoUrl()).apply();
            }
        }
    }

    public void restoreUserAddedVideos(SharedPreferences sharedPreferences) {
        List<Video> videoList = videoManager.getVideoList();
        for (Video video : videoList) {
            if (video.getId().startsWith("new_")) {
                String videoUrl = sharedPreferences.getString(video.getId() + "_videoUrl", null);
                if (videoUrl != null) {
                    video.setVideoUrl(videoUrl);
                }
            }
        }
        loadVideos();
    }

    public void loadVideoData(Context context, SharedPreferences sharedPreferences) {
        try {
            if (videoManager.getVideoList().isEmpty()) {
                InputStream inputStream = context.getAssets().open("videos.json");
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                String json = new String(buffer, "UTF-8");
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id = obj.getString("id");
                    String title = obj.getString("title");
                    String author = obj.getString("author");
                    int views = obj.getInt("views");
                    String uploadTime = obj.getString("uploadTime");
                    String thumbnailUrl = obj.getString("thumbnailUrl");
                    String authorProfilePicUrl = obj.getString("authorProfilePicUrl");
                    String videoUrl = obj.getString("videoUrl");
                    String category = obj.getString("category");
                    int likes = obj.getInt("likes");

                    int updatedViews = getUpdatedViews(id, views, sharedPreferences);
                    int updatedLikes = getUpdatedLikes(id, likes, sharedPreferences);

                    videoManager.getLikesCountMap().put(id, updatedLikes);
                    videoManager.getLikedStateMap().put(id, sharedPreferences.getBoolean(id + "_liked", false));

                    Video video = new Video(id, title, author, updatedViews, uploadTime, thumbnailUrl, authorProfilePicUrl, videoUrl, category, updatedLikes);
                    if (videoManager.getVideoById(id) != null) {
                        videoManager.updateVideo(video);
                    } else {
                        videoManager.addVideo(video);
                    }
                }
            } else {
                loadVideos();
            }
        } catch (Exception e) {
            Log.e("MainViewModel", "Error loading video data", e);
        }
    }

    private int getUpdatedViews(String videoId, int defaultViews, SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(videoId + "_views", defaultViews);
    }

    private int getUpdatedLikes(String videoId, int defaultLikes, SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(videoId + "_likes", defaultLikes);
    }

    public void filterVideos(String query) {
        List<Video> filteredList = new ArrayList<>();
        for (Video video : videoManager.getVideoList()) {
            if (video.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    video.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(video);
            }
        }
        videos.postValue(filteredList);
    }

    public void filterVideosByCategory(String category) {
        List<Video> filteredList = new ArrayList<>();
        for (Video video : videoManager.getVideoList()) {
            if (video.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(video);
            }
        }
        videos.postValue(filteredList);
    }

    public void updateVideoViews(String videoId, int updatedViews) {
        Video video = videoManager.getVideoById(videoId);
        if (video != null) {
            video.setViews(updatedViews);
            videoManager.updateVideo(video);
            loadVideos();
        }
    }

    public void loadVideosFromDatabase() {
        List<Video> videoList = videoManager.getVideoList();
        if (videoList.isEmpty()) {
            loadVideoData(getApplication(), getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE));
        } else {
            videos.postValue(videoList);
        }
    }


}