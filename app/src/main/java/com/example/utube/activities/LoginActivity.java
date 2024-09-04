package com.example.utube.activities;

import static com.example.utube.activities.MainActivity.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.utube.R;
import com.example.utube.models.UserDetails;
import com.example.utube.viewmodels.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private UserViewModel userViewModel;
    private final UserDetails userDetails = UserDetails.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load theme from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        // Initialize the UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe the authentication result
        userViewModel.getAuthenticateResult().observe(this, isAuthenticated -> {
            if (isAuthenticated != null && isAuthenticated) {
                //Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                userViewModel.fetchUserDetails(userDetails);
            } else {
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe user details fetch result
        userViewModel.getUserDetails().observe(this, userDetails -> {
            if (userDetails != null) {
                // Log.e("LoginActivity", "User details fetched successfully " + this.userDetails.getUsername() + " ," + this.userDetails.getProfilePic());

                createUserThread();
                userViewModel.getThreadCreationStatus().observe(this, isCreated -> {
                    if (isCreated == null || !isCreated) {
                        Toast.makeText(LoginActivity.this, "Failed to create user thread", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.d("LoginActivity", "User thread created successfully");
                    }
                });

                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("USERNAME", this.userDetails.getUsername());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for loginButton
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            userViewModel.authenticate(username, password);
        });

        // Set onClickListener for registerButton
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void createUserThread() {
        String token = userDetails.getToken();
        userViewModel.createUserThread(token);
    }

}


//package com.example.utube.activities;
//
//import static com.example.utube.activities.MainActivity.PREFS_NAME;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.utube.R;
//import com.example.utube.models.Users;
//import com.example.utube.models.Users.User;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText usernameEditText;
//    private EditText passwordEditText;
//    private Button loginButton;
//    private Button registerButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // Load theme from shared preferences
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
//        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        usernameEditText = findViewById(R.id.username);
//        passwordEditText = findViewById(R.id.password);
//        loginButton = findViewById(R.id.login_button);
//        registerButton = findViewById(R.id.register_button);
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = usernameEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//                if (validateLogin(username, password)) {
//                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("USERNAME", username);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//            }
//        });
//    }
//
//    private boolean validateLogin(String username, String password) {
////        User user = Users.getInstance().getUser(username);
////        return user != null && user.getPassword().equals(password);
//        return Users.getInstance().validateUser(username, password);
//    }
//}
//package com.example.utube.activities;
//
//import static com.example.utube.activities.MainActivity.PREFS_NAME;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.utube.R;
//import com.example.utube.models.UserDetails;
//import com.example.utube.viewmodels.UserViewModel;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText usernameEditText;
//    private EditText passwordEditText;
//    private Button loginButton;
//    private Button registerButton;
//    private UserViewModel userViewModel;
//    private final UserDetails userDetails = UserDetails.getInstance();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Load theme from shared preferences
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
//        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
//
//        setContentView(R.layout.activity_login);
//
//        usernameEditText = findViewById(R.id.username);
//        passwordEditText = findViewById(R.id.password);
//        loginButton = findViewById(R.id.login_button);
//        registerButton = findViewById(R.id.register_button);
//
//        // Initialize the UserViewModel
//        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
//
//        // Observe the authentication result
//        userViewModel.getAuthenticateResult().observe(this, isAuthenticated -> {
//            if (isAuthenticated != null && isAuthenticated) {
//                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                userViewModel.fetchUserDetails(usernameEditText.getText().toString());
//            } else {
//                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Observe user details fetch result
//        userViewModel.getUserDetails().observe(this, userDetails -> {
//            if (userDetails != null) {
//                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                Toast.makeText(LoginActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Set onClickListener for loginButton
//        loginButton.setOnClickListener(v -> {
//            String username = usernameEditText.getText().toString();
//            String password = passwordEditText.getText().toString();
//            userViewModel.authenticate(username, password);
//        });
//
//        // Set onClickListener for registerButton
//        registerButton.setOnClickListener(v -> {
//            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//        });
//    }
//}
//
//
////package com.example.utube.activities;
////
////import static com.example.utube.activities.MainActivity.PREFS_NAME;
////
////import android.content.Intent;
////import android.content.SharedPreferences;
////import android.os.Bundle;
////import android.view.View;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.Toast;
////
////import androidx.appcompat.app.AppCompatActivity;
////
////import com.example.utube.R;
////import com.example.utube.models.Users;
////import com.example.utube.models.Users.User;
////
////public class LoginActivity extends AppCompatActivity {
////
////    private EditText usernameEditText;
////    private EditText passwordEditText;
////    private Button loginButton;
////    private Button registerButton;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        // Load theme from shared preferences
////        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
////        boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
////        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
////
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_login);
////
////        usernameEditText = findViewById(R.id.username);
////        passwordEditText = findViewById(R.id.password);
////        loginButton = findViewById(R.id.login_button);
////        registerButton = findViewById(R.id.register_button);
////
////        loginButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                String username = usernameEditText.getText().toString();
////                String password = passwordEditText.getText().toString();
////                if (validateLogin(username, password)) {
////                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
////                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
////                    intent.putExtra("USERNAME", username);
////                    startActivity(intent);
////                    finish();
////                } else {
////                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
////
////
////        registerButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
////            }
////        });
////    }
////
////    private boolean validateLogin(String username, String password) {
//////        User user = Users.getInstance().getUser(username);
//////        return user != null && user.getPassword().equals(password);
////        return Users.getInstance().validateUser(username, password);
////    }
////}
