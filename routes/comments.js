// routes/comments.js
const express = require('express');
const commentsController = require('../controllers/comments');
const router = express.Router();

router.post('/videos/:videoId/comments', commentsController.addComment);
router.delete('/videos/:videoId/comments/:commentId', commentsController.deleteComment);

module.exports = router;
