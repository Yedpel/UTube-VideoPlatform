package com.example.utube.models;

import java.util.HashMap;
import java.util.Map;

public class Users {

    // Singleton instance
    private static Users instance;

    // HashMap to store user data
    private Map<String, User> usersMap;

    // Private constructor to prevent instantiation
    private Users() {
        usersMap = new HashMap<>();
    }

    // Method to get the singleton instance
    public static synchronized Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }

    // Method to add a new user
    public void addUser(String username, String password, String firstName, String lastName, String dob, String email, String profilePic) {
        if (!usersMap.containsKey(username)) {
            User newUser = new User(username, password, firstName, lastName, dob, email, profilePic);
            usersMap.put(username, newUser);
        } else {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    // Method to get user by username
    public User getUser(String username) {
        return usersMap.get(username);
    }

    // Method to check if user exists
    public boolean userExists(String username) {
        return usersMap.containsKey(username);
    }

    // Method to validate user login
    public boolean validateUser(String username, String password) {
        User user = usersMap.get(username);
        return user != null && user.getPassword().equals(password);
    }

    // User class to store individual user details
    public static class User {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String dob;
        private String email;
        private String profilePic;

        public User(String username, String password, String firstName, String lastName, String dob, String email, String profilePic) {
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.dob = dob;
            this.email = email;
            this.profilePic = profilePic;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getDob() {
            return dob;
        }

        public String getEmail() {
            return email;
        }

        public String getProfilePic() {
            return profilePic;
        }
    }
}
