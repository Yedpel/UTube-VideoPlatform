// services/comments.js
import Comment from '../models/comments.js';
import Video from '../models/videoPlay.js';  
//import mongoose from 'mongoose';
import mongoose from 'mongoose';


export async function createCommentModel(commentData) {
    try {
        // Create and save the new comment
        const newComment = new Comment(commentData);
        const savedComment = await newComment.save();

        // Push the saved comment's ID to the corresponding video's comments array
        const updatedVideo = await Video.findByIdAndUpdate(
            commentData.videoId,
            { $push: { comments: savedComment._id } },
            { new: true }
        );

        if (!updatedVideo) {
            // If the video couldn't be updated, rollback the comment creation
            await Comment.findByIdAndDelete(savedComment._id);
            throw new Error('Video not found; Comment was not added');
        }

        return savedComment;
    } catch (error) {
        throw new Error('Error creating comment: ' + error.message);
    }
}