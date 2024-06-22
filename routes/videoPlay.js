import express from 'express';
import { getVideos, getVideo, createVideo, fetchComments, fetchCommentCount } from '../controllers/videoPlay.js'
//import { isLoggedIn} from '../controllers/login.js'
const router = express.Router();

router.get('/', getVideos);

//watch video
router.get('/videoPlay/:id', getVideo);

//upload video
// router.post('/videoPlay', getVideo);

// Route to upload a new video
router.post('/videoPlay', createVideo);


// Route to get all videos with enhanced author details
// router.get('/', getVideosWithAuthorDetails);

// Route to get a single video with enhanced author details
router.get('/video/:videoId/comments', fetchComments);
router.get('/video/:videoId/comments/count', fetchCommentCount);

export default router;
