import express from 'express';
import { getVideos, getVideo, createVideo} from '../controllers/videoPlay.js'
//import { isLoggedIn} from '../controllers/login.js'
const router = express.Router();

//router.get('/', getVideos);

//watch video
router.get('/videoPlay/:id', getVideo);

//upload video
// router.post('/videoPlay', getVideo);

// Route to upload a new video
router.post('/videoPlay', createVideo);

export default router;
