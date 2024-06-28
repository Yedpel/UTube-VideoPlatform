import express from 'express';
import {
    getVideos, getVideo, createVideo, fetchComments, fetchCommentCount, fetchMixedVideos, fetchVideosByCategory
    , updateVideo, deleteVideo, likeVideo, UnlikeVideo, getVideosByUserId,
    getUserLikedVideo
} from '../controllers/videoPlay.js'
import { isLoggedIn } from '../controllers/tokens.js';
import { addComment, deleteComment, likeComment, unlikeComment, updateComment } from '../controllers/comments.js';
import { isUserLikedVideo } from '../services/videoPlay.js';
//import { isLoggedIn} from '../controllers/login.js'
const router = express.Router();


//////videos list page/////

// Route to get a mix of 20 videos (or less if there are less than 20) from the database
router.get('/videos', fetchMixedVideos);

//get user videos
router.get('/users/:id/videos', getVideosByUserId);

// Route to get videos by category (can implement also on client side, from the videos list page he got)
// need to update the real adress, depand on how the button will be on client side//
router.get('/videos/category/:category', fetchVideosByCategory);

//search videos is on client side (search bar), from the videos list page he got

///actions on videos////

// Route to upload a new video
router.post('/users/:id/videos', isLoggedIn, createVideo);

//update video
router.put('/users/:id/videos/:pid', isLoggedIn, updateVideo);

//delete video
router.delete('/users/:id/videos/:pid', isLoggedIn, deleteVideo);

//////watch video page/////

//get a video to watch without comments (comments will be fetched by next routes)
router.get('/users/:id/videos/:pid', getVideo);

//////comments data on videos///// (actions of comments are on routes/comments.js)

//get the comments list of a video
router.get('/users/:id/videos/:pid/comments', fetchComments);

//get the count of comments of a video
router.get('/users/:id/videos/:pid/comments/count', fetchCommentCount);

///likes on videos////

//get ahead if a user liked the video or not
router.get('/users/:id/videos/:pid', isLoggedIn, getUserLikedVideo);

//a route to like a video
router.put('/users/:id/videos/:pid/likes', isLoggedIn, likeVideo);

//a route to unlike a video
router.put('/users/:id/videos/:pid/unlikes', isLoggedIn, UnlikeVideo);


export default router;






// // Route to get a single video with enhanced author details
// router.get('/video/:videoId/comments', fetchComments); //maybe 

// Route to get all the comment 
// router.get('/users/:id/video/:pid/comments/count', fetchCommentCount); 


// Route to get all videos with enhanced author details
// router.get('/', getVideosWithAuthorDetails);








