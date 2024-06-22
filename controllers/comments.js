// controllers/comments.js
const commentsService = require('../services/comments');

async function addComment(req, res) {
    try {
        const videoId = req.params.videoId;
        const comment = req.body;
        const updatedVideo = await commentsService.addComment(videoId, comment);
        res.status(200).json(updatedVideo);
    } catch (error) {
        res.status(500).send(error.message);
    }
}

async function deleteComment(req, res) {
    try {
        const videoId = req.params.videoId;
        const commentId = req.params.commentId;
        const updatedVideo = await commentsService.deleteComment(videoId, commentId);
        res.status(200).json(updatedVideo);
    } catch (error) {
        res.status(500).send(error.message);
    }
}

module.exports = { addComment, deleteComment };
