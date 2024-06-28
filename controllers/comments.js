// controllers/comments.js
const commentsService = require('../services/comments');

async function addComment(req, res) {
    try {
        const pid = req.params.pid;
        const comment = req.body;
        const userId = req.user.userId;

        const updatedVideo = await commentsService.addComment(pid, userId, comment);
        res.status(200).json(updatedVideo);
    } catch (error) {
        res.status(500).send(error.message);
    }
}

async function deleteComment(req, res) {
    try {
        const pid = req.params.pid;
        const commentId = req.params.commentId;
        const updatedVideo = await commentsService.deleteComment(pid, commentId);
        res.status(200).json(updatedVideo);
    } catch (error) {
        res.status(500).send(error.message);
    }
}
export const updateComment = async (req, res) => {
    const { cid } = req.params;
    const { content } = req.body;

    try {
        const comment = await commentsService.updateComment(cid, content);
        res.status(200).json(comment);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

export const likeComment = async (req, res) => {
    const { pid, cid } = req.params;
    const userId = req.user.userId;

    try {
        const comment = await commentsService.likeComment(pid, cid, userId);
        res.status(200).json(comment);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

export const unlikeComment = async (req, res) => {
    const { pid, cid } = req.params;
    const userId = req.user.userId;

    try {
        const comment = await commentsService.unlikeComment(pid, cid, userId);
        res.status(200).json(comment);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

module.exports = { addComment, deleteComment };
