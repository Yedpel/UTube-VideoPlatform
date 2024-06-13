package com.example.utube.activities;

import static com.example.utube.activities.MainActivity.PREFS_NAME;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.utube.R;
import com.example.utube.models.Users;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText dobEditText;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private ImageView profilePicImageView;
    private Button selectProfilePicButton;
    private Button registerButton;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        dobEditText = findViewById(R.id.dob);
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        profilePicImageView = findViewById(R.id.profile_pic);
        selectProfilePicButton = findViewById(R.id.select_profile_pic_button);
        registerButton = findViewById(R.id.register_button);

        selectProfilePicButton.setOnClickListener(v -> openImagePicker());

        registerButton.setOnClickListener(v -> {
            if (validateRegistration()) {
                Users.getInstance().addUser(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        dobEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        //set profile pic to placeholder if no image is selected, and to the selected image string if an image is selected
                        selectedImageUri != null ? selectedImageUri.toString() : "android.resource://com.example.utube/drawable/ic_profile_pic"
                );
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(RegisterActivity.this, "Invalid Registration Details", Toast.LENGTH_SHORT).show();
            }
        });

        //try9
        dobEditText.setOnClickListener(v -> { //try9
            Calendar calendar = Calendar.getInstance(); //try9
            int year = calendar.get(Calendar.YEAR); //try9
            int month = calendar.get(Calendar.MONTH); //try9
            int day = calendar.get(Calendar.DAY_OF_MONTH); //try9

            DatePickerDialog datePickerDialog = new DatePickerDialog( //try9
                    RegisterActivity.this, //try9
                    (view, year1, monthOfYear, dayOfMonth) -> dobEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), //try9
                    year, month, day); //try9

            datePickerDialog.show(); //try9
        }); //try9

        // Inside onCreate method
        dobEditText.setInputType(InputType.TYPE_NULL); // Prevents keyboard from popping up //try10
        dobEditText.setFocusable(false); // Prevents focus on the EditText //try10

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profilePicImageView.setImageURI(selectedImageUri);
        }
    }

    private boolean validateRegistration() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
//
//        //if email address is ilegal mark it red and return false
//        if (!email.contains("@") || !email.contains(".")) {
//            emailEditText.setError("Invalid email address");
//            return false;
//        }
//        //if password and confirm password are not the same mark them red and return false
//        if (!password.equals(confirmPassword)) {
//            passwordEditText.setError("Passwords do not match");
//            confirmPasswordEditText.setError("Passwords do not match");
//            return false;
//        }
//
//        // Check if the username already exists //try8
//        if (Users.getInstance().getUser(username) != null) { //try8
//            usernameEditText.setError("Username already taken"); //try8
//            return false; //try8
//        } //try8
//
//        //check that dob is valid
//        if (dobEditText.getText().toString().isEmpty()) {
//            dobEditText.setError("Please select a date");
//            return false;
//        }
//        //check that all fields are filled except profile pic
//        return !firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()
//                && !username.isEmpty() && password.equals(confirmPassword) ;

        return true;

    }
}
