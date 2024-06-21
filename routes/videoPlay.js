import express from 'express';
import { getVideos, getVideo} from '../controllers/videoPlay.js'
//import { isLoggedIn} from '../controllers/login.js'
const router = express.Router();

//router.get('/', getVideos);

//watch video
router.get('/videoPlay/:id', getVideo);

//upload video
router.post('/videoPlay', getVideo);


export default router;
