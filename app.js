import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors';
import fs from 'fs';
import mongoose from 'mongoose';
import customEnv from 'custom-env';

import routerVideoPlay from './routes/videoplay.js';
import routerSignUp from './routes/signUp.js';
import routerLogin from './routes/login.js';
import User from './models/users.js';
import Video from './models/videoPlay.js';
import userRouter from './routes/users.js';  // Adjust path as necessary
import { createVideoModel, updateVideoModel, deleteVideoModel, likeVideo, unlikeVideo } from './services/videoPlay.js'; // Make sure updateVideoModel is imported
import { registerUser } from './controllers/signUp.js'; // Make sure to import registerUser
import { updateUserModel, deleteUserModel } from './services/users.js'; // Make sure updateUserModel and deleteUserModel are imported

//add dotenv for environment variables
import dotenv from 'dotenv';
dotenv.config();

// Environment variables
customEnv.env(process.env.NODE_ENV || 'local', './config');

// MongoDB connection
mongoose.connect(process.env.CONNECTION_STRING, { useNewUrlParser: true, useUnifiedTopology: true })
       .then(() => {
           console.log('MongoDB connected');
           testLikeAndUnlikeFeatures();
       })
    .catch(err => console.error('MongoDB connection error:', err));

// Express app setup
const server = express();
server.use(express.static('public'));
server.use(bodyParser.urlencoded({ extended: true }));
server.use(cors());
server.set('view engine', 'ejs');
server.set('views', './views');

// Routes
server.use('/videoPlay', routerVideoPlay);
server.use('/login', routerLogin);
server.use('/signUp', routerSignUp);
server.use('/users', userRouter);


// Load initial data if no data exists in MongoDB
async function checkAndLoadData() {
    const userExists = await User.findOne();
    const videoExists = await Video.findOne();

    if (!userExists && !videoExists) {
        console.log("No data found in MongoDB, loading initial data...");
        loadData();
    } else {
        console.log("Data already exists in MongoDB, skipping initial load.");
    }
}

// Load initial data into MongoDB
async function loadData() {
    try {
        // Read users and videos data from JSON files
        const usersData = JSON.parse(fs.readFileSync('./users.json', 'utf8'));
        const videosData = JSON.parse(fs.readFileSync('./videos.json', 'utf8'));

        // Clear existing data and load new data
        await User.deleteMany({});
        await User.insertMany(usersData);
        await Video.deleteMany({});
        await Video.insertMany(videosData);
    } catch (err) {
        console.error('Failed to load data:', err);
    }
}


/////////////////////tests///////////////////// and below the start of the server listen


async function testLikeAndUnlikeFeatures() {
    const videoId = '6676af630402f0c497e29d93';
    const userId = '6676af630402f0c497e29d8c';

    try {
        // const likedVideo = await likeVideo(videoId, userId);
        // console.log('Video liked:', likedVideo);

        const unlikedVideo = await unlikeVideo(videoId, userId);
        console.log('Video unliked:', unlikedVideo);
    } catch (error) {
        console.error('Failed to like/unlike video:', error);
    }
}


// Test user registration
async function testRegisterUser() {
    // Simulate request and response
    const req = {
        body: {
            firstName: "Test",
            lastName: "User",
            date: "1990-01-01",
            email: "testuser@example.com",
            profilePic: "url_to_pic",
            username: "testuser",
            password: "password123",
            passwordConfirm: "password123"
        }
    };
    const res = {
        status: (statusCode) => {
            console.log(`Status Code: ${statusCode}`);
            return {
                json: (data) => {
                    console.log('Response:', data);
                },
                send: (data) => {
                    console.log('Response:', data);
                }
            };
        }
    };

    await registerUser(req, res);
}

//test update and delete user manually
async function updateAndDeleteSampleUser() {
    const userId = '6676b7785c00cb8e630072f5';  // Use a valid user ID
    try {
        await updateUserModel(userId, { firstName: 'New Name' });  // Change attributes as needed
        console.log('User updated');
        await deleteUserModel(userId);
        console.log('User deleted');
    } catch (error) {
        console.error('Error updating or deleting user:', error);
    }
}


// test manually Add a sample video to the database to test the API
async function addSampleVideo() {
    const sampleVideoData = {
        thumbnailUrl: "drawable/imvid23",
        title: "tryAdd",
        author: "Author 2",
        authorProfilePic: "drawable/pro_2",  // Corrected field name
        views: 0,
        uploadTime: new Date(),  // Current date/time or specific date
        videoUrl: "raw/vid23_oly08",
        category: "News",
        likes: 0,
        comments: []  // Empty array if no comments
    };

    try {
        const newVideo = await createVideoModel(sampleVideoData);
        console.log('Sample video added:', newVideo);
    } catch (error) {
        console.error('Failed to add sample video:', error);
    }
}
// test manually Update the sample video added earlier
async function updateSampleVideo() {
    const videoId = '6676a61dafc32dbcb2a532c4';  // Replace with the actual ID
    try {
        const updatedVideo = await updateVideoModel(videoId, { title: 'tryEdit' });
        console.log('Video updated:', updatedVideo);
    } catch (error) {
        console.error('Failed to update video:', error);
    }
}
// test manually Delete the sample video added earlier
async function deleteSampleVideo() {
    const videoId = '6676aa67d97f7d21b433c0e3';  // Replace with the actual ID
    try {
        const deletedVideo = await deleteVideoModel(videoId);
        console.log('Video deleted:', deletedVideo);
    } catch (error) {
        console.error('Failed to delete video:', error);
    }
}


// Start the server
const PORT = process.env.PORT || 89;
server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});



// import express from 'express';
// import bodyParser from 'body-parser';
// import routerVideoPlay from './routes/videoplay.js';
// import routerSignUp from './routes/signUp.js';
// import routerLogin from './routes/login.js';
// import cors from 'cors';
// import customEnv from 'custom-env';
// import mongoose from 'mongoose';

// //import session from 'express-session';
// customEnv.env(process.env.NODE_ENV, './config');

// mongoose.connect(process.env.CONNECTION_SRTING, { useNewUrlParser: true, useUnifiedTopology: true })
//     .then(() => console.log('MongoDB connected'))
//     .catch(err => console.error('MongoDB connection error:', err));

// server.use(express.static('public'));
// server.use(bodyParser.urlencoded({ extended: true }));
// server.set('view engine', 'ejs');
// server.set('views', './views');
// server.use(cors());
// server.use('/videoPlay', routerVideoPlay);
// server.use('/login', routerLogin);
// server.use('/signUp', routerSignUp);

// const PORT = process.env.PORT || 89;
// server.listen(PORT, () => {
//     console.log(`Server is running on port ${PORT}`);
// });