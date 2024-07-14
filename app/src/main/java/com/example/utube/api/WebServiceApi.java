package com.example.utube.api;

import com.example.utube.models.UserDetails;
import com.example.utube.models.Video;
import com.example.utube.utils.LoginRequest;
import com.example.utube.utils.LoginResponse;
import com.example.utube.utils.VideoResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface WebServiceApi {
    @POST("tokens")
    Call<LoginResponse> checkUserNameAndPassword(@Body LoginRequest loginRequest);

    //    @GET("users/{id}")
//    Call<UserDetails> getUser(@Path("id") String id);
    @GET("users/{id}")
    Call<UserDetails> getUser(@Path("id") String id, @Header("Authorization") String token);

    @Multipart
    @POST("users")
    Call<UserDetails> signUp(
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("date") RequestBody date,
            @Part("email") RequestBody email,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profilePic
    );


    @GET("videos")
    Call<List<VideoResponse>> getVideos();

    @GET("users/{userId}/videos/{videoId}")
    Call<VideoResponse> getVideo(@Path("videoId") String videoId);

}
