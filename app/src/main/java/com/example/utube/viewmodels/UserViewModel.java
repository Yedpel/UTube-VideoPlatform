package com.example.utube.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.utube.api.UserApi;
import com.example.utube.models.UserDetails;

public class UserViewModel extends ViewModel {
    private UserApi userApi;
    private MutableLiveData<Boolean> authenticateResult;
    private MutableLiveData<UserDetails> userDetails;

    private MutableLiveData<Boolean> registrationStatus; // Add this field

    public UserViewModel() {
        authenticateResult = new MutableLiveData<>();
        userDetails = new MutableLiveData<>();
        registrationStatus = new MutableLiveData<>(); // Add this line
        userApi = new UserApi(authenticateResult, userDetails, registrationStatus); // Change this line
    }

    public MutableLiveData<Boolean> getRegistrationStatus() {
        return registrationStatus;
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

    public void fetchUserDetails(UserDetails user) {
        userApi.fetchUserDetails(user);
    }

    public void signUp(UserDetails user) {
        userApi.signUp(user);
    }

    public void updateUserDetails(UserDetails user) {
        userApi.updateUserDetails(user);
    }
}
//package com.example.utube.viewmodels;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.utube.api.UserApi;
//import com.example.utube.models.UserDetails;
//
//public class UserViewModel extends ViewModel {
//    private UserApi userApi;
//    private MutableLiveData<Boolean> authenticateResult;
//    private MutableLiveData<UserDetails> userDetails;
//
//    public UserViewModel() {
//        authenticateResult = new MutableLiveData<>();
//        userDetails = new MutableLiveData<>();
//        userApi = new UserApi(authenticateResult, userDetails);
//    }
//
//    public LiveData<Boolean> getAuthenticateResult() {
//        return authenticateResult;
//    }
//
//    public LiveData<UserDetails> getUserDetails() {
//        return userDetails;
//    }
//
//    public void authenticate(String username, String password) {
//        userApi.authenticate(username, password);
//    }
//
//    public void fetchUserDetails(String username) {
//        userApi.fetchUserDetails(username);
//    }
//}
