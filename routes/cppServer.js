import express from 'express';
import { notifyVideoWatch, createUserThread, closeUserThread } from '../controllers/cppServerController.js';
import { isLoggedIn } from '../middlewares/auth.js';

const router = express.Router();

router.post('/notify-watch', notifyVideoWatch);

router.post('/create-thread', isLoggedIn, createUserThread);

router.post('/close-thread', isLoggedIn, closeUserThread);

export default router;