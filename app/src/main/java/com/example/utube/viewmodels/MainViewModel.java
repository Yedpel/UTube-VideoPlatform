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
import com.example.utube.data.VideoRepository;
import com.example.utube.models.Video;
import com.example.utube.utils.VideoResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<Video>> videos;
    private VideoManager videoManager;
    private VideoRepository videoRepository;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> error = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        videoManager = VideoManager.getInstance(application);
        videos = new MutableLiveData<>();
        //  loadVideos();  // Load videos immediately on ViewModel creation
        videoRepository = new VideoRepository(application);
        isLoading = new MutableLiveData<>(false);
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Video>> getVideos() {
//        if (videos.getValue() == null) {
//            loadVideos();
//        }
        return videos;
    }

    //    public void loadVideos() {
//        List<Video> videoList = videoManager.getVideoList();
//        Log.d("MainViewModel", "Loaded " + videoList.size() + " videos");
//        for (Video video : videoList) {
//            Log.d("MainViewModel", "Video " + video.getId() + " has " + video.getViews() + " views");
//        }
//        videos.postValue(videoList != null ? videoList : new ArrayList<>());
//    }
    public void loadVideos() {
        isLoading.setValue(true);
        List<Video> videoList = videoManager.getVideoList();
        if (videoList.isEmpty()) {
            fetchVideosFromServer();
        } else {
            videos.postValue(videoList);
            isLoading.postValue(false);
            fetchVideosFromServer(); // Fetch in background to update
        }
    }

    private void fetchVideosFromServer() {
        isLoading.setValue(true);
        videoRepository.fetchVideosFromServer(new Callback<List<VideoResponse>>() {
            @Override
            public void onResponse(Call<List<VideoResponse>> call, Response<List<VideoResponse>> response) {
                Log.d("MainViewModel", "onResponse called. isSuccessful: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    List<Video> newVideos = convertToVideoList(response.body());
                    Log.d("MainViewModel", "Received " + newVideos.size() + " videos from server");
                    videoManager.setVideoList(newVideos);
                    videos.postValue(newVideos);
                } else {
                    Log.e("MainViewModel", "Response not successful. Code: " + response.code() + ", Message: " + response.message());
                    error.postValue("Failed to load videos. Please try again.");
                }
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Call<List<VideoResponse>> call, Throwable t) {
                Log.e("MainViewModel", "Network request failed", t);
                error.postValue("Network error. Please check your connection and try again.");
                isLoading.postValue(false);
            }
        });
    }

    private List<Video> convertToVideoList(List<VideoResponse> videoResponses) {
        List<Video> videoList = new ArrayList<>();
        for (VideoResponse response : videoResponses) {
            Video video = new Video(
                    response.getId(),
                    response.getTitle(),
                    response.getAuthor(),
                    response.getViews(),
                    response.getUploadTime(),
                    response.getThumbnailUrl(),
                    response.getAuthorProfilePic(),
                    response.getVideoUrl() != null ? response.getVideoUrl() : "",
                    //"",  // videoUrl
                    response.getCategory(),
                    0  // likes
            );
            videoList.add(video);
        }
        return videoList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void refreshVideos() {
        fetchVideosFromServer();
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

    //    public void loadVideosFromDatabase() {
//        List<Video> videoList = videoManager.getVideoList();
//        if (videoList.isEmpty()) {
//            loadVideoData(getApplication(), getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE));
//        } else {
//            for (Video video : videoList) {
//                Log.d("MainViewModel", "Video " + video.getId() + " has " + video.getViews() + " views");
//            }
//            videos.postValue(videoList);
//        }
//    }
    public void loadVideosFromDatabase() {
        List<Video> videoList = videoManager.getVideoList();
        if (videoList.isEmpty()) {
            fetchVideosFromServer();
        } else {
            videos.postValue(videoList);
            fetchVideosFromServer(); // Fetch in background to update
        }
    }


}