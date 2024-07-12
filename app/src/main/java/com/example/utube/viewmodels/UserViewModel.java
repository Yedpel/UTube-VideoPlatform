package com.example.utube.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.utube.data.UserApi;
import com.example.utube.models.UserDetails;

public class UserViewModel extends ViewModel {
    private UserApi userApi;
    private MutableLiveData<Boolean> authenticateResult;
    private MutableLiveData<UserDetails> userDetails;

    public UserViewModel() {
        authenticateResult = new MutableLiveData<>();
        userDetails = new MutableLiveData<>();
        userApi = new UserApi(authenticateResult, userDetails);
    }

    public LiveData<Boolean> getAuthenticateResult() {
        return authenticateResult;
    }

    public LiveData<UserDetails> getUserDetails() {
        return userDetails;
    }

    public void authenticate(String username, String password) {
        userApi.authenticate(username, password);
    }

    public void fetchUserDetails(String username) {
        userApi.fetchUserDetails(username);
    }
}
