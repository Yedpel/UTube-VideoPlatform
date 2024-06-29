import express from 'express';
import { upload } from './mediaRoutes.js'; 
import {
    getVideos, getVideo, createVideo, fetchComments, fetchCommentCount, fetchMixedVideos, fetchVideosByCategory
    , updateVideo, deleteVideo, likeVideo, UnlikeVideo, getVideosByUserId,
    getUserLikedVideo, addView, getWatchPageData
} from '../controllers/videoPlay.js'
//import { isLoggedIn } from '../controllers/tokens.js';
import { addComment, deleteComment, likeComment, unlikeComment, updateComment } from '../controllers/comments.js';
import { isUserLikedVideo } from '../services/videoPlay.js';
//import { isLoggedIn} from '../controllers/login.js'
import { isLoggedIn } from '../middlewares/auth.js';
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
//router.post('/users/:id/videos', isLoggedIn, createVideo);
// Updated route to handle video and thumbnail upload
router.post('/users/:id/videos', isLoggedIn, upload.fields([{ name: 'video', maxCount: 1 }, { name: 'thumbnail', maxCount: 1 }]), createVideo);

//update video
//router.put('/users/:id/videos/:pid', isLoggedIn, updateVideo);
// Update route to handle video and thumbnail replacement
router.put('/users/:id/videos/:pid', isLoggedIn, upload.fields([{ name: 'video', maxCount: 1 }, { name: 'thumbnail', maxCount: 1 }]), updateVideo);

//delete video
router.delete('/users/:id/videos/:pid', isLoggedIn, deleteVideo);

//////watch video page/////

// Route to fetch all watch page data
router.get('/users/:id/videos/:pid', getWatchPageData);

//get a video to watch without comments (comments will be fetched by next routes)
router.get('/users/:id/videos/:pid', getVideo);

// Route to increment video views (need to decied how to implement it on client side, maybe)
router.get('/users/:id/videos/:pid', addView);
///code for react is on the end of the file///

//////comments data on videos///// (actions of comments are on routes/comments.js)

//get the comments list of a video
router.get('/users/:id/videos/:pid/comments', fetchComments);

//get the count of comments of a video
router.get('/users/:id/videos/:pid/comments/count', fetchCommentCount);

///likes on videos////

//get ahead if a user liked the video or not (return false on guest)
router.get('/users/:id/videos/:pid', isLoggedIn, getUserLikedVideo);

//a route to like a video
router.put('/users/:id/videos/:pid/likes', isLoggedIn, likeVideo);

//a route to unlike a video
router.put('/users/:id/videos/:pid/unlikes', isLoggedIn, UnlikeVideo);


export default router;





///code for react to see the updated views without refreshing the page///
/*
fetch(`/api/users/${userId}/videos/${videoId}`)
  .then(response => response.json())
  .then(data => {
    if(data.message === 'View added successfully') {
      setVideoDetails(prevDetails => ({ ...prevDetails, views: data.views }));
    }
  })
  .catch(error => console.error('Error updating views:', error));
    */
///end of code for react to see the updated views without refreshing the page///


  ///old routes for videos and comments////
// // Route to get a single video with enhanced author details
// router.get('/video/:videoId/comments', fetchComments); //maybe 

// Route to get all the comment 
// router.get('/users/:id/video/:pid/comments/count', fetchCommentCount); 


// Route to get all videos with enhanced author details
// router.get('/', getVideosWithAuthorDetails);








