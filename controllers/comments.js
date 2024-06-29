// controllers/comments.js
//const commentsService = require('../services/comments');
import commentsService from '../services/comments.js';
//import all the services from the comments.js
import {
    createCommentModel, editCommentModel, deleteCommentModel, LikeComment, UnlikeComment,
    isUserLikedComment
} from '../services/comments.js';

export const addComment = async (req, res) => {
    try {
        const pid = req.params.pid;
        const comment = req.body;
        const userId = req.user.userId;

        const updatedVideo = await createCommentModel(pid, userId, comment);
        res.status(200).json(updatedVideo);
    } catch (error) {
        res.status(500).send(error.message);
    }
}

export const deleteComment = async (req, res) => {
    try {
        const pid = req.params.pid;
        const commentId = req.params.commentId;
        const updatedVideo = await deleteCommentModel(pid, commentId);
        res.status(200).json(updatedVideo);
    } catch (error) {
        res.status(500).send(error.message);
    }
}

export const updateComment = async (req, res) => {
    const { cid } = req.params;
    const { content } = req.body;

    try {
        const comment = await editCommentModel(cid, content);
        res.status(200).json(comment);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

//deal with get isUserLikedcomment in services/comments.js
export const getUserLikedComment = async (req, res) => {
    const { pid, cid } = req.params;
    const userId = req.user.userId;
    //if it is a guest, then isLiked will be false
    if (!userId) {
        return res.json({ isLiked: false });
    }

    try {
        const comment = await isUserLikedComment(pid, cid, userId);
        res.status(200).json(comment);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};


export const likeComment = async (req, res) => {
    const { pid, cid } = req.params;
    const userId = req.user.userId;

    try {
        const comment = await LikeComment(pid, cid, userId);
        res.status(200).json(comment);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

export const unlikeComment = async (req, res) => {
    const { pid, cid } = req.params;
    const userId = req.user.userId;

    try {
        const comment = await UnlikeComment(pid, cid, userId);
        res.status(200).json(comment);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

//module.exports = { addComment, deleteComment };
