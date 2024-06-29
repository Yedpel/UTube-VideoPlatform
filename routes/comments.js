// routes/comments.js
//const express = require('express');
import express from 'express'

//const commentsController = require('../controllers/comments');
const router = express.Router();

import e from 'express';
import {
    addComment, deleteComment, likeComment, unlikeComment, updateComment,
    getUserLikedComment
} from '../controllers/comments.js';

import { isLoggedIn } from '../middlewares/auth.js';


///get comments of a video and count is on routes/videoPlay.js///

//////actions on comments/////

//a route to add a comment on video
router.post('/users/:id/videos/:pid/comments', isLoggedIn, addComment);

//a route to update a comment of videos
router.put('/users/:id/videos/:pid/comments/cid', isLoggedIn, updateComment);

//a route to delete a comment of videos
router.delete('/users/:id/videos/:pid/comments/cid', isLoggedIn, deleteComment);

//get ahead if a user liked the comment or not
router.get('/users/:id/videos/:pid/comments/cid', isLoggedIn, getUserLikedComment);

//a route to like a comment of videos
router.put('/users/:id/videos/:pid/comments/cid/like', isLoggedIn, likeComment);

//a route to unlike a comment of videos
router.put('/users/:id/videos/:pid/comments/cid/unLike', isLoggedIn, unlikeComment);


export default router;
//module.exports = router;



// router.post('/videos/:videoId/comments', commentsController.addComment);
// router.delete('/videos/:videoId/comments/:commentId', commentsController.deleteComment);