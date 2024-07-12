package com.example.utube.viewmodels;

import android.app.Application;
import android.net.Uri;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AddVideoViewModel extends AndroidViewModel {
    private MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
    private MutableLiveData<String[]> categories = new MutableLiveData<>();

    public AddVideoViewModel(Application application) {
        super(application);
        loadCategories();
    }

    private void loadCategories() {
        String[] categoriesArray = {"Sport", "News", "Cinema", "Gaming"};
        categories.setValue(categoriesArray);
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

    public boolean isInputValid(String title, String category) {
        return !title.isEmpty() && !category.isEmpty() && selectedImageUri.getValue() != null;
    }
}