import express from 'express';
import { getVideos, getVideo, createVideo, fetchComments, fetchCommentCount,fetchMixedVideos, fetchVideosByCategory } from '../controllers/videoPlay.js'
import { isLoggedIn } from '../controllers/tokens.js';
import { addComment, deleteComment, likeComment, UnlikeComment, Updatecomment } from '../controllers/comments.js';
//import { isLoggedIn} from '../controllers/login.js'
const router = express.Router();



// Route to get a mix of videos
router.get('/videos', fetchMixedVideos);

// Route to get videos by category
router.get('/videos/category/:category', fetchVideosByCategory);

//update video
router.put('/users/:id/videos/:pid',isLoggedIn, updateVideo);

//get the video of the user
router.delete('/users/:id/videos/:pid',isLoggedIn, deleteVideo); 

// Route to upload a new video
router.post('/users/:id/videos',isLoggedIn, createVideo);

//get user videos
router.get('/users/:id/videos', getUserVideos);//need to craete this function 

//watch video page

//get a video to watch with all the comments
router.get('/users/:id/videos/:pid', getVideo);

//a route to like a video
router.put('/users/:id/videos/:pid/likes',isLoggedIn,likeVideo);

//a route to unlike a video
//router.put('/users/:id/videos/:pid/unlikes',isLoggedIn,UnlikeVideo);

//a route to add a comment of videos
router.post('/users/:id/videos/:pid/comments',isLoggedIn ,addComment);

//a route to update a comment of videos
router.put('/users/:id/videos/:pid/comments/cid',isLoggedIn ,Updatecomment);

//a route to delte a comment of videos
router.delete('/users/:id/videos/:pid/comments/cid',isLoggedIn,deleteComment);


//a route to update a comment of videos
router.put('/users/:id/videos/:pid/comments/cid/like',isLoggedIn ,likeComment);

//a route to update a comment of videos
router.put('/users/:id/videos/:pid/comments/cid/unLike',isLoggedIn ,UnlikeComment);








// // Route to get a single video with enhanced author details
// router.get('/video/:videoId/comments', fetchComments); //maybe 

// Route to get all the comment 
// router.get('/users/:id/video/:pid/comments/count', fetchCommentCount); 


// Route to get all videos with enhanced author details
// router.get('/', getVideosWithAuthorDetails);




export default router;









