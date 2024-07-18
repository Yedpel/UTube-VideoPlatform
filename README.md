# UTUBE_Android

Welcome to the UTUBE Android App repository!
This project was developed as part of the Advanced System Programming course at Bar-Ilan
University (BIU).

## Project Team

This application is the result of collaboration among three students:

- Yedidya Peles
- Shimon Rahamim
- Avrham Bicha

We faced many challenges during the development process but ultimately succeeded in creating a
functional and user-friendly application.

## About the App

UTUBE is an application designed for sharing videos, inspired by the popular YouTube platform. It is
developed for both Android and Web platforms. This repository contains the Android part of the
application.

## Branches

- **Main branch**: This branch contains the original version of the Android app compatible with Task
  1, which is a local UTUBE app without server connectivity.
- **task3-android barnch**: This branch (current) contains the updated version of the app compatible
  with Task 3, designed to work with a server.

## Experience and Challenges

Throughout our journey, we gained valuable experience and encountered numerous challenging problems,
which we managed to overcome with determination and teamwork. We hope you find this app useful and
enjoyable as much as we enjoyed developing it!

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for
development and testing purposes. Follow these simple steps to get started.

### Prerequisites

1. Ensure you have Android Studio installed on your computer. If not, download and install it
   from [Android Studio's official website](https://developer.android.com/studio).
2. Make sure you have the UTUBE-Server barnch server_task3 set up and running. If not, follow the
   instructions in the server's README to set it up.

### Setting up the Project

1. **Clone the Repository**
   Open your terminal and run the following command:
   ```bash
   git clone https://github.com/Yedpel/UTube_Android.git -b task3-android
   cd UTube_Android

2. **Open the Project in Android Studio**
    - Open Android Studio. On the welcome screen, select Open an Existing Project or
      go to File > Open... if you have another project open.
    - Navigate to the directory where you cloned the project and select it.

3. **Configure Server Connection**
    - Open the app/src/main/java/com/example/utube/network/ApiClient.kt file (or similar
      configuration file).
    - Update the BASE_URL constant with your server's IP address and port:

      ```bash
      const val BASE_URL = "http://your_server_ip:12345/"
   Replace your_server_ip with the actual IP address of the machine running the UTUBE-Server.

4. **Run the Application**
    - After the project opens, let Android Studio build the project. If there are any dependencies
      to be downloaded, Android Studio will manage this automatically.

    - To run the app, choose an emulator or connect an Android device to your computer.

    - Click on the Run button (green triangle) in the toolbar. Android Studio will build
      the application and install it on the selected device or emulator.

**Running the Project with the Server**
1.Ensure the UTUBE-Server (Task 3 Compatible) is running. Refer to the server's README for
instructions on starting the server.

2.Make sure your Android device or emulator is connected to the same network as the server.

3.Run the Android app as described in step 4 of "Setting up the Project".

4.The app should now be able to communicate with the server, allowing you to access all features
that require server connectivity.

**Troubleshooting**

- If you encounter any issues with building or running the app,ensure your Android SDK is up-to-date
  and that you have the correct build tools installed. Check the build.gradle file for any specific
  SDK or library dependency that might need attention.
- If the app cannot connect to the server, double-check that the server is running
  and that the BASE_URL in the app's configuration is correct. Also, ensure that your firewall
  is not blocking the connection.
- For any server-related issues, refer to the UTUBE-Server README for troubleshooting steps.
  **Contributing**
  We welcome contributions to improve the UTUBE Android app. Feel free to fork the repository,
  make your changes, and submit a pull request.

---
© 2024 Yedidya Peles, Shimon Rahamim, Avrham Bicha. All Rights Reserved.