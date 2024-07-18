package com.example.utube.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.utube.api.VideoApi;

import java.io.File;

public class AddVideoViewModel extends AndroidViewModel {
    private MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
    private MutableLiveData<String[]> categories = new MutableLiveData<>();
    private MutableLiveData<Boolean> uploadStatus = new MutableLiveData<>();
    private VideoApi videoApi;

    public AddVideoViewModel(Application application) {
        super(application);
        loadCategories();
        videoApi = new VideoApi(uploadStatus, null, null);
    }

    private void loadCategories() {
        String[] categoriesArray = {"Sport", "News", "Cinema", "Gaming"};
        categories.setValue(categoriesArray);
    }

    public void setCategories(MutableLiveData<String[]> categories) {
        this.categories = categories;
    }

    public void setUploadStatus(MutableLiveData<Boolean> uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public void setSelectedImageUri(Uri uri) {
        selectedImageUri.setValue(uri);
    }

    public LiveData<Uri> getSelectedImageUri() {
        return selectedImageUri;
    }

    public LiveData<String[]> getCategories() {
        return categories;
    }

    public void uploadVideo(String title, String category, File videoFile, File thumbnailFile, String userId, String authorName, String token) {
        if (videoFile != null && thumbnailFile != null) {
            videoApi.uploadVideo(title, category, videoFile, thumbnailFile, userId, authorName, token);
        } else {
            uploadStatus.postValue(false);
        }
    }
//    public void uploadVideo(String title, String category, Uri videoUri, Uri thumbnailUri, String userId, String authorName, String token) {
//        String videoPath = getRealPathFromUri(getApplication(), videoUri);
//        String thumbnailPath = getRealPathFromUri(getApplication(), thumbnailUri);
//        videoApi.uploadVideo(title, category, videoPath, thumbnailPath, userId, authorName, token);
//    }

    //    private String getRealPathFromUri(Context context, Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//        if (cursor == null) return null;
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String path = cursor.getString(column_index);
//        cursor.close();
//        return path;
//    }
    public boolean isInputValid(String title, String category) {
        return !title.isEmpty() && !category.isEmpty() && selectedImageUri.getValue() != null;
    }

    public MutableLiveData<Boolean> getUploadStatus() {
        return uploadStatus;
    }

}
//package com.example.utube.viewmodels;
//
//import android.app.Application;
//import android.net.Uri;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//public class AddVideoViewModel extends AndroidViewModel {
//    private MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
//    private MutableLiveData<String[]> categories = new MutableLiveData<>();
//
//    public AddVideoViewModel(Application application) {
//        super(application);
//        loadCategories();
//    }
//
//    private void loadCategories() {
//        String[] categoriesArray = {"Sport", "News", "Cinema", "Gaming"};
//        categories.setValue(categoriesArray);
//    }
//
//    public void setSelectedImageUri(Uri uri) {
//        selectedImageUri.setValue(uri);
//    }
//
//    public LiveData<Uri> getSelectedImageUri() {
//        return selectedImageUri;
//    }
//
//    public LiveData<String[]> getCategories() {
//        return categories;
//    }
//
//    public boolean isInputValid(String title, String category) {
//        return !title.isEmpty() && !category.isEmpty() && selectedImageUri.getValue() != null;
//    }
//}