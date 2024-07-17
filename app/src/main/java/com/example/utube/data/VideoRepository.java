package com.example.utube.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.utube.api.RetrofitClient;
import com.example.utube.api.WebServiceApi;
import com.example.utube.models.Video;
import com.example.utube.models.VideoEntity;
import com.example.utube.utils.VideoResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void updateVideoFromModel(Video video) {
        executorService.execute(() -> {
            VideoEntity videoEntity = convertVideoToEntity(video);
            videoDao.updateVideo(videoEntity);
            Log.d("VideoRepository", "Updated video in database from model: " + video.getId() + " with views: " + video.getViews());
        });
    }

    private VideoEntity convertVideoToEntity(Video video) {
        return new VideoEntity(
                video.getId(),
                video.getTitle(),
                video.getAuthor(),
                video.getViews(),
                video.getUploadTime(),
                video.getThumbnailUrl(),
                video.getAuthorProfilePicUrl(),
                video.getVideoUrl(),
                video.getCategory(),
                video.getLikes()
        );
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

    public void fetchVideosFromServer(Callback<List<VideoResponse>> callback) {
        try {
            WebServiceApi webServiceApi = RetrofitClient.getInstance().create(WebServiceApi.class);
            Call<List<VideoResponse>> call = webServiceApi.getVideos();
            Log.d("VideoRepository", "Sending request to server");
            call.enqueue(new Callback<List<VideoResponse>>() {
                @Override
                public void onResponse(Call<List<VideoResponse>> call, Response<List<VideoResponse>> response) {
                    Log.d("VideoRepository", "Received response from server. isSuccessful: " + response.isSuccessful());
                    if (response.isSuccessful() && response.body() != null) {
                        List<VideoResponse> videos = response.body();
                        Log.d("VideoRepository", "Raw JSON response: " + new Gson().toJson(videos));
                        Log.d("VideoRepository", "Received " + videos.size() + " videos from server");
                        insertVideosFromServer(videos);
                        callback.onResponse(call, response);
                    } else {
                        Log.e("VideoRepository", "Response not successful. Code: " + response.code() + ", Message: " + response.message());
                        callback.onFailure(call, new Throwable("Error fetching videos"));
                    }
                }

                @Override
                public void onFailure(Call<List<VideoResponse>> call, Throwable t) {
                    Log.e("VideoRepository", "Network request failed", t);
                    callback.onFailure(call, t);
                }
            });
        } catch (Exception e) {
            Log.e("VideoRepository", "Error creating network request", e);
            callback.onFailure(null, new Throwable("Error creating network request: " + e.getMessage()));
        }
    }

    private void insertVideosFromServer(List<VideoResponse> videos) {
        executorService.execute(() -> {
            List<VideoEntity> entities = new ArrayList<>();
            for (VideoResponse video : videos) {
                VideoEntity videoEntity = new VideoEntity(
                        video.getId(),
                        video.getTitle(),
                        video.getAuthor(),
                        video.getViews(),
                        video.getUploadTime(),
                        video.getThumbnailUrl(),
                        video.getAuthorProfilePic(),
                        "",
                        video.getCategory(),
                        0
                );
                videoDao.insert(videoEntity);
                entities.add(videoEntity);
            }
        });
    }

    public LiveData<List<VideoEntity>> getVideosFromRoom() {
        return videoDao.getAllVideosLive();
    }

    public boolean isLocalDataEmpty() {
        try {
            return executorService.submit(() -> videoDao.getCount() == 0).get();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

//    public void fetchVideoDetailsFromServer(String videoId, Callback<VideoResponse> callback) {
//        WebServiceApi webServiceApi = RetrofitClient.getInstance().create(WebServiceApi.class);
//        Call<VideoResponse> call = webServiceApi.getVideo(videoId);
//        call.enqueue(callback);
//    }

    public void fetchVideoDetailsFromServer(String videoId, Callback<VideoResponse> callback) {
        try {
            WebServiceApi webServiceApi = RetrofitClient.getInstance().create(WebServiceApi.class);
            Call<VideoResponse> call = webServiceApi.getVideo(videoId);
            call.enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        VideoResponse video = response.body();
                        Log.d("VideoRepository", "Raw JSON response for video " + videoId + ": " + new Gson().toJson(video));
                        callback.onResponse(call, response);
                    } else {
                        Log.e("VideoRepository", "Response not successful. Code: " + response.code() + ", Message: " + response.message());
                        callback.onFailure(call, new Throwable("Error fetching video details"));
                    }
                }

                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {
                    Log.e("VideoRepository", "Network request failed", t);
                    callback.onFailure(call, t);
                }
            });
        } catch (Exception e) {
            Log.e("VideoRepository", "Error creating network request", e);
            callback.onFailure(null, new Throwable("Error creating network request: " + e.getMessage()));
        }
    }

    //try-channle-server
    public void fetchVideosByUsernameFromServer(String username, Callback<List<VideoResponse>> callback) {
        try {
            WebServiceApi webServiceApi = RetrofitClient.getInstance().create(WebServiceApi.class);
            Call<List<VideoResponse>> call = webServiceApi.getVideosByUsername(username);
            Log.d("VideoRepository", "Sending request to server for user: " + username);
            call.enqueue(new Callback<List<VideoResponse>>() {
                @Override
                public void onResponse(Call<List<VideoResponse>> call, Response<List<VideoResponse>> response) {
                    Log.d("VideoRepository", "Received response from server for user: " + username + ". isSuccessful: " + response.isSuccessful());
                    if (response.isSuccessful() && response.body() != null) {
                        List<VideoResponse> videos = response.body();
                        Log.d("VideoRepository", "Raw JSON response for user " + username + ": " + new Gson().toJson(videos));
                        Log.d("VideoRepository", "Received " + videos.size() + " videos from server for user: " + username);
                        insertVideosFromServer(videos);
                        callback.onResponse(call, response);
                    } else {
                        Log.e("VideoRepository", "Response not successful for user: " + username + ". Code: " + response.code() + ", Message: " + response.message());
                        callback.onFailure(call, new Throwable("Error fetching videos for user: " + username));
                    }
                }

                @Override
                public void onFailure(Call<List<VideoResponse>> call, Throwable t) {
                    Log.e("VideoRepository", "Network request failed for user: " + username, t);
                    callback.onFailure(call, t);
                }
            });
        } catch (Exception e) {
            Log.e("VideoRepository", "Error creating network request for user: " + username, e);
            callback.onFailure(null, new Throwable("Error creating network request: " + e.getMessage()));
        }
    }

}