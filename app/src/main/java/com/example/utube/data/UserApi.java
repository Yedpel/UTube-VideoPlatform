package com.example.utube.data;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.utube.MyApplication;
import com.example.utube.models.UserDetails;
import com.example.utube.utils.LoginRequest;
import com.example.utube.utils.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserApi {
    private MutableLiveData<Boolean> authenticateResult;
    private MutableLiveData<UserDetails> userData;
    public Retrofit retrofit;
    public WebServiceApi webApi;

    public UserApi(MutableLiveData<Boolean> authenticateResult, MutableLiveData<UserDetails> userData) {
        this.authenticateResult = authenticateResult;
        this.userData = userData;

        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:12345/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.webApi = retrofit.create(WebServiceApi.class);
    }

    public void authenticate(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> call = webApi.checkUserNameAndPassword(loginRequest);
        Log.d("UserApi", "Authenticating user: " + username);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d("UserApi", "Authentication failed: " + response.code());
                    Toast.makeText(MyApplication.context, "User Not Found!", Toast.LENGTH_SHORT).show();
                    authenticateResult.setValue(false);
                } else {
                    Log.d("UserApi", "Authentication successful" + response.body());
                    LoginResponse loginResponse = response.body();
                    UserDetails userDetails = UserDetails.getInstance();
                    userDetails.setUsername(username);
                    userDetails.setToken(loginResponse.getToken());
                    userDetails.set_id(loginResponse.getUserId());
                    authenticateResult.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("UserApi", "Authentication request failed", t);
                Toast.makeText(MyApplication.context, "Unable to connect to the server.", Toast.LENGTH_SHORT).show();
                authenticateResult.setValue(false);
            }
        });
    }

    public void fetchUserDetails(String username) {
        //send a request to the server to get user details, by send token and id
        //in the route, and then set the user details in the userDetails object
    }
//        Call<UserDetails> call = webApi.getUser(username);
//        call.enqueue(new Callback<UserDetails>() {
//            @Override
//            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
//                if (response.isSuccessful()) {
//                    userData.setValue(response.body());
//                } else {
//                    Log.d("UserApi", "Failed to fetch user details: " + response.code());
//                    userData.setValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserDetails> call, Throwable t) {
//                Log.e("UserApi", "User details request failed", t);
//                userData.setValue(null);
//            }
//        });
}

