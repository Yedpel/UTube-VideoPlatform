import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors';
import fs from 'fs';
import mongoose from 'mongoose';
import customEnv from 'custom-env';
//add dotenv for environment variables
import dotenv from 'dotenv';
// mediaRoutes is for uploading and replacing media files
import mediaRoutes from './routes/mediaRoutes.js'; 
// import jwt from 'jsonwebtoken';



import routerVideoPlay from './routes/videoplay.js';
import routerSignUp from './routes/signUp.js';
import routerLogin from './routes/tokens.js';
import userRouter from './routes/users.js';  

import User from './models/users.js';
import Video from './models/videoPlay.js';
import {
    createVideoModel, updateVideoModel, deleteVideoModel, likeVideo, unlikeVideo, isUserLikedVideo,
    isUserTheAuthor, getVideosByUserId
} from './services/videoPlay.js'; 
import { registerUser } from './controllers/signUp.js'; 
import { updateUserModel, deleteUserModel } from './services/users.js'; 
import {
    createCommentModel, editCommentModel, deleteCommentModel, isUserTheAuthorOfComment,
    likeComment, unlikeComment, isUserLikedComment, getCommentsByVideoId, countCommentsByVideoId
} from './services/comments.js';
// import { checkUserNameAndPassword } from './services/tokens.js'; 
import { fetchMixedVideos ,fetchVideosByCategory } from './controllers/videoPlay.js';


dotenv.config();

// Environment variables
customEnv.env(process.env.NODE_ENV || 'local', './config');

const server = express();

(async ()=> {
// MongoDB connection
mongoose.connect(process.env.CONNECTION_STRING, { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => {
        console.log('MongoDB connected');
        checkAndLoadData();  // check if the mongoDB is empty and load the data
    })
    .catch(err => console.error('MongoDB connection error:', err));

    // Express app setup
    server.use(express.static('public'));
    server.use(bodyParser.urlencoded({ extended: true }));
    server.use(cors());
    server.use(express.json({ limit: '10mb' }) )
    // server.set('view engine', 'ejs');
    // server.set('views', './views');

    // Routes
    server.use('/api/users', userRouter);

    server.use('/api/tokens', routerLogin);
    
    server.use('/api/videos', routerVideoPlay);
    server.use('/signUp', routerSignUp);
    server.use('/media', mediaRoutes); 

})()

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

async function loadData() {
    try {
        const usersData = JSON.parse(fs.readFileSync('./users.json', 'utf8'));
        const videosData = JSON.parse(fs.readFileSync('./videos.json', 'utf8'));

        await User.deleteMany({});
        const createdUsers = await User.insertMany(usersData);

        // Create a map of usernames to user IDs
        const usernameToIdMap = createdUsers.reduce((map, user) => {
            map[user.username] = user._id;
            return map;
        }, {});

        // Transform video data to include authorId instead of author username
        const transformedVideos = videosData.map(video => ({
            ...video,
            authorId: usernameToIdMap[video.author],  // Map the username to ObjectId
            authorName: video.author,  // Optionally keep authorName if needed
            uploadTime: new Date() // Convert to actual Date if necessary
        }));

        await Video.deleteMany({});
        await Video.insertMany(transformedVideos);
        console.log('Data loaded successfully');
    } catch (err) {
        console.error('Failed to load data:', err);
    }
}




// Load initial data into MongoDB
// async function loadData() {
//     try {
//         // Read users and videos data from JSON files
//         const usersData = JSON.parse(fs.readFileSync('./users.json', 'utf8'));
//         const videosData = JSON.parse(fs.readFileSync('./videos.json', 'utf8'));

//         // Clear existing data and load new data
//         await User.deleteMany({});
//         await User.insertMany(usersData);
//         await Video.deleteMany({});
//         await Video.insertMany(videosData);
//     } catch (err) {
//         console.error('Failed to load data:', err);
//     }
// }


/////////////////////tests///////////////////// and below the start of the server listen
// Function to test fetching videos by category
async function testFetchVideosByCategory() {
    const fakeReq = {
        params: {
            category: 'Sport'  // Change to the category you'd like to test
        }
    };
    const fakeRes = {
        json: (data) => console.log("Test Fetch Videos By Category:", data),
        status: function (statusCode) {
            console.log(`HTTP Status: ${statusCode}`);
            return this;  // Allow method chaining
        },
        send: (data) => console.log(data)
    };

    try {
        await fetchVideosByCategory(fakeReq, fakeRes);
    } catch (err) {
        console.error('Error during fetchVideosByCategory test:', err);
    }
}

// Function to test fetching mixed videos
async function testFetchMixedVideos() {
    const fakeReq = {};  // Mock request object, add properties if your controller uses them
    const fakeRes = {
        json: (data) => console.log("Test Fetch Mixed Videos:", data),
        status: function (statusCode) {
            console.log(`HTTP Status: ${statusCode}`);
            return this;  // Allow method chaining
        },
        send: (data) => console.log(data)
    };

    try {
        await fetchMixedVideos(fakeReq, fakeRes);
    } catch (err) {
        console.error('Error during fetchMixedVideos test:', err);
    }
}


// async function testLogin() {
//     try {
//         const isValid = await checkUserNameAndPassword("Author 1", "Author 2");
//         console.log(`Login valid: ${isValid}`);
//     } catch (error) {
//         console.error('Error during login test:', error);
//     }
// }


// // test delete user
// async function testDeleteUser(userId) {
//     try {
//         console.log(`Attempting to delete user with ID: ${userId}`);
//         const user = await deleteUserModel(userId);
//         if (user) {
//             console.log(`User with ID: ${userId} was deleted successfully.`);
//         } else {
//             console.log(`No user found with ID: ${userId}.`);
//         }
//     } catch (error) {
//         console.error('Failed to delete user:', error);
//     }
// }

// async function testFetchComments() {
//     const videoId = '66771d56ee7de545aba5a4a1';  // Replace with an actual video ID from your database

//     try {
//         console.log(`Fetching comments for video ID: ${videoId}`);
//         const comments = await getCommentsByVideoId(videoId);
//         console.log(`Comments for video ${videoId}:`, comments);
//         comments.forEach(comment => {
//             console.log(`- Comment text: "${comment.text}" by User ID: ${comment.userId}`);
//         });
//     } catch (error) {
//         console.error('Error fetching comments:', error);
//     }
// }

// async function testFetchCommentCount() {
//     const videoId = '66771d56ee7de545aba5a4a1';  // Replace with an actual video ID from your database

//     try {
//         console.log(`Fetching comment count for video ID: ${videoId}`);
//         const count = await countCommentsByVideoId(videoId);
//         console.log(`Total comments for video ${videoId}: ${count}`);
//     } catch (error) {
//         console.error('Error fetching comment count:', error);
//     }
// }


// async function testIsUserLikedComment() {
//     const commentId = "667722b7e4d39c07e840d0c7";  // Replace with an actual comment ID
//     const userId = "66771d56ee7de545aba5a49a";  // Replace with an actual user ID
//     const userId2 = "66771d56ee7de545aba5a49b";  // Replace with an actual user ID
//     try {
//         const hasLiked = await isUserLikedComment(commentId, userId2);
//         console.log(`Has user ${userId2} liked comment ${commentId}? ${hasLiked}`);
//         // hasLiked = await isUserLikedComment(commentId, userId2);
//         // console.log(`Has user ${userId2} liked comment ${commentId}? ${hasLiked}`);
//     } catch (error) {
//         console.error('Failed to check if user is the liker of the comment:', error);
//     }
// }

// async function testLikeAndUnlikeComment() {
//     const commentId = "667722b7e4d39c07e840d0c7";  // Replace with an actual comment ID
//     const userId = "66771d56ee7de545aba5a49a";  // Replace with an actual user ID

//     try {
//         console.log(`Liking comment ${commentId} by user ${userId}`);
//         const likedComment = await likeComment(commentId, userId);
//         console.log('Comment liked:', likedComment);

//         // console.log(`Unliking comment ${commentId} by user ${userId}`);
//         // const unlikedComment = await unlikeComment(commentId, userId);
//         // console.log('Comment unliked:', unlikedComment);
//     } catch (error) {
//         console.error('Failed to like or unlike comment:', error);
//     }
// }

// async function testIsUserTheAuthorOfComment() {
//     const commentId = "667722b7e4d39c07e840d0c7";  // Replace with an actual comment ID
//     const userId = "66771d56ee7de545aba5a49a";  // Replace with an actual user ID

//     try {
//         const isAuthor = await isUserTheAuthorOfComment(commentId, userId);
//         console.log(`1- Is user ${userId} the author of comment ${commentId}? ${isAuthor}`);
//     } catch (error) {
//         console.error('1 -Failed to check if user is the author of the comment:', error);
//     }
// }

// async function testIsUserTheAuthorOfComment2() {
//     const commentId = "667722b7e4d39c07e840d0c7";  // Replace with an actual comment ID
//     const userId = "66771d56ee7de545aba5a49c";  // Replace with an actual user ID

//     try {
//         const isAuthor = await isUserTheAuthorOfComment(commentId, userId);
//         console.log(`2 -Is user ${userId} the author of comment ${commentId}? ${isAuthor}`);
//     } catch (error) {
//         console.error('2 -Failed to check if user is the author of the comment:', error);
//     }
// }

// // Testing comment deletion
// async function testDeleteComment() {
//     const commentId = "66771ea4fef6b2921ffe42f6";  // Replace with an actual comment ID

//     try {
//         const result = await deleteCommentModel(commentId);
//         console.log(result.message);
//     } catch (error) {
//         console.error('Failed to delete comment:', error);
//     }
// }

// // Testing comment editing
// async function testEditComment() {
//     const commentId = "66771ea4fef6b2921ffe42f6";  // Replace with an actual comment ID
//     const newText = "Updated text for this comment.";

//     try {
//         const updatedComment = await editCommentModel(commentId, newText);
//         console.log('Comment updated:', updatedComment);
//     } catch (error) {
//         console.error('Failed to update comment:', error);
//     }
// }

// // Testing comment creation
// async function testCreateComment() {
//     const commentData = {
//         userId: "66782b4d5939f8d3739fc64f",  // Example user ID
//         videoId: "66782b4d5939f8d3739fc656",  // Example video ID
//         text: "Great video!",
//         uploadTime: new Date("2020-01-02T15:00:00Z"),
//         likes: 0,
//         likedByUsers: []
//     };

//     try {
//         const newComment = await createCommentModel(commentData);
//         console.log('Comment created and added to video:', newComment);
//     } catch (error) {
//         console.error('Failed to create comment:', error);
//     }
// }


// async function testDeleteUserAndVideos(userId) {
//     try {
//         console.log(`Checking videos before deletion for user ${userId}`);
//         const videosBefore = await getVideosByUserId(userId);
//         console.log(`Found ${videosBefore.length} videos before deletion.`);

//         const deletedUser = await deleteUserModel(userId);
//         if (deletedUser) {
//             console.log(`User deleted successfully. ID: ${userId}`);
//         }

//         console.log(`Checking videos after deletion for user ${userId}`);
//         const videosAfter = await getVideosByUserId(userId);
//         console.log(`Found ${videosAfter.length} videos after deletion.`);
//     } catch (error) {
//         console.error('Error in testDeleteUserAndVideos:', error);
//     }
// }



// async function testAuthorship() {
//     const videoId = "6676faa2de0663d0aaa2a23b";
//     const userId = "6676faa2de0663d0aaa2a234";

//     try {
//         const isAuthor = await isUserTheAuthor(videoId, userId);
//         console.log(`1 - Is user ${userId} the author of video ${videoId}? ${isAuthor}`);
//     } catch (error) {
//         console.error('Error checking authorship:', error);
//     }
// }

// async function testAuthorship2() {
//     const videoId = "6676faa2de0663d0aaa2a23b";
//     const userId = "6676faa2de0663d0aaa2a235";

//     try {
//         const isAuthor = await isUserTheAuthor(videoId, userId);
//         console.log(`2 - Is user ${userId} the author of video ${videoId}? ${isAuthor}`);
//     } catch (error) {
//         console.error('Error checking authorship:', error);
//     }
// }


// async function testUserLikedVideo() {
//     const videoId = '6676af630402f0c497e29d94';
//     const userId = '6676af630402f0c497e29d8c';

//     try {
//         const hasLiked = await isUserLikedVideo(videoId, userId);
//         console.log(`Has user ${userId} liked video ${videoId}?`, hasLiked);
//     } catch (error) {
//         console.error('Error checking if user liked video:', error);
//     }
// }

// async function testLikeAndUnlikeFeatures() {
//     const videoId = '6676af630402f0c497e29d93';
//     const userId = '6676af630402f0c497e29d8c';

//     try {
//         // const likedVideo = await likeVideo(videoId, userId);
//         // console.log('Video liked:', likedVideo);

//         const unlikedVideo = await unlikeVideo(videoId, userId);
//         console.log('Video unliked:', unlikedVideo);
//     } catch (error) {
//         console.error('Failed to like/unlike video:', error);
//     }
// }


// // Test user registration
// async function testRegisterUser() {
//     // Simulate request and response
//     const req = {
//         body: {
//             firstName: "Test",
//             lastName: "User",
//             date: "1990-01-01",
//             email: "testuser@example.com",
//             profilePic: "url_to_pic",
//             username: "testuser",
//             password: "password123",
//             passwordConfirm: "password123"
//         }
//     };
//     const res = {
//         status: (statusCode) => {
//             console.log(`Status Code: ${statusCode}`);
//             return {
//                 json: (data) => {
//                     console.log('Response:', data);
//                 },
//                 send: (data) => {
//                     console.log('Response:', data);
//                 }
//             };
//         }
//     };

//     await registerUser(req, res);
// }

// //test update and delete user manually
// async function updateAndDeleteSampleUser() {
//     const userId = '6676b7785c00cb8e630072f5';  // Use a valid user ID
//     try {
//         await updateUserModel(userId, { firstName: 'New Name' });  // Change attributes as needed
//         console.log('User updated');
//         await deleteUserModel(userId);
//         console.log('User deleted');
//     } catch (error) {
//         console.error('Error updating or deleting user:', error);
//     }
// }


// // test manually Add a sample video to the database to test the API
// async function addSampleVideo() {
//     const sampleVideoData = {
//         thumbnailUrl: "drawable/imvid23",
//         title: "tryAdd",
//         authorId: "6676f39196de4690f086aa54",
//         views: 0,
//         uploadTime: new Date(),  // Current date/time or specific date
//         videoUrl: "raw/vid23_oly08",
//         category: "News",
//         likes: 0,
//         comments: []  // Empty array if no comments
//     };

//     try {
//         const newVideo = await createVideoModel(sampleVideoData);
//         console.log('Sample video added:', newVideo);
//     } catch (error) {
//         console.error('Failed to add sample video:', error);
//     }
// }
// // test manually Update the sample video added earlier
// async function updateSampleVideo() {
//     const videoId = '6676a61dafc32dbcb2a532c4';  // Replace with the actual ID
//     try {
//         const updatedVideo = await updateVideoModel(videoId, { title: 'tryEdit' });
//         console.log('Video updated:', updatedVideo);
//     } catch (error) {
//         console.error('Failed to update video:', error);
//     }
// }
// // test manually Delete the sample video added earlier
// async function deleteSampleVideo() {
//     const videoId = '6676aa67d97f7d21b433c0e3';  // Replace with the actual ID
//     try {
//         const deletedVideo = await deleteVideoModel(videoId);
//         console.log('Video deleted:', deletedVideo);
//     } catch (error) {
//         console.error('Failed to delete video:', error);
//     }
// }



/////////////////////////end of tests///////////////////////

// Start the server
const PORT = process.env.PORT || 89;
server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
