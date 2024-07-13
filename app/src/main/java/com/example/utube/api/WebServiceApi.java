package com.example.utube.api;

import com.example.utube.models.UserDetails;
import com.example.utube.models.Video;
import com.example.utube.utils.LoginRequest;
import com.example.utube.utils.LoginResponse;
import com.example.utube.utils.VideoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebServiceApi {
    @POST("tokens")
    Call<LoginResponse> checkUserNameAndPassword(@Body LoginRequest loginRequest);

    @GET("users/{id}")
    Call<UserDetails> getUser(@Path("id") String id);

    @GET("videos")
    Call<List<VideoResponse>> getVideos();


}
