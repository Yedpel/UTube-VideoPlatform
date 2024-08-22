import express from 'express';
import { notifyVideoWatch, createUserThread, closeUserThread } from '../controllers/cppServerController.js';
import { isLoggedIn } from '../middlewares/auth.js';

const router = express.Router();

//update the cp DB that the user has watched the video
//the req need to have the video id and the token
router.post('/notify-watch', isLoggedIn, notifyVideoWatch);

//after authentication, create a thread for the user
//the req need to have the token
router.post('/create-thread', isLoggedIn, createUserThread);

//before logging out, close the user thread
//the req need to have the token
router.post('/close-thread', isLoggedIn, closeUserThread);


export default router;