# UTube Node.js Server

## Overview
Welcome to the UTube Node.js Server branch, a key component of the UTube app developed as part of an Advanced System Programming course at Bar-Ilan University. This repository holds the server-side code that powers our UTube application, which simulates a simplified version of a video streaming service where users can watch, like, and comment on videos.

## Full Project Details
For full details on the entire UTube project, including the Android app, React web app, and C++ server, please skip this README.md and refer to the wiki pages located in the the wiki folder .

### Features
The server handles a variety of functions including:
- User authentication and management
- Video uploads, updates, and fetching
- Comment system with support for likes
- Video recommendations based on views and categories

## Getting Started
To set up and run the server locally, follow these steps:

1. **Clone the repository**
   First, clone the repository and switch to the correct branch:
   ```bash
   git clone https://github.com/[username]/task4_UTube.git
   cd task4_UTube
   git checkout NodeJs_Server_updated_2
3. **Install dependencies**
Ensure that you have Node.js installed on your system. Then run:
   ```bash
   npm install
   npm i express
4. **Environment Setup**
Create a .env file in the root directory and add the necessary environment variables:
    ```makefile
    CONNECTION_STRING=mongodb://localhost:27017/UTube
    PORT=12345
    
5. Run the application
Start the server using:
   ```bash
    npm start

Contribution
This project benefits from the collective efforts of our team, combining diverse ideas and coding practices to create a functional and engaging application.

Feel free to explore the code and suggest improvements or enhancements by submitting pull requests or opening issues.

Thank you for visiting our project!

---
© 2024 Yedidya Peles, Shimon Rahamim, Avrham Bicha. All Rights Reserved.

