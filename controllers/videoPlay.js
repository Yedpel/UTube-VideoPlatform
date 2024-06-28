import { getVideoModel, createVideoModel, getVideosModel, updateVideoModel, deleteVideoModel,
     getVideosWithAuthorDetails,getMixedVideos, getVideosByCategory } from '../services/videoPlay.js';
import { getCommentsByVideoId, countCommentsByVideoId } from '../services/comments.js';


export async function getVideos(req, res) {
    try {
      //  const videos = await getVideosModel();
      const videos = await getVideosWithAuthorDetails();
        res.render('allVideos', { videos });
    } catch (error) {
        console.error('Error fetching videos with author details:', error);
        res.status(500).send('Failed to retrieve videos');
    }
}

export async function getVideo(req, res) {
    try {
        const video = await getVideoModel(req.params.id);
        if (video) {
            res.render('video', { video });
        } else {
            res.status(404).send('Video not found');
        }
    } catch (error) {
        res.status(500).send('Error retrieving video');
    }
}

export async function createVideo(req, res) {
    try {
        await createVideoModel(req.body);
        res.redirect('/videoPlay');
    } catch (error) {
        res.status(500).send('Failed to create video');
    }
}

export async function updateVideo(req, res) {
    try {
        const updatedVideo = await updateVideoModel(req.params.id, req.body);
        if (updatedVideo) {
            res.send('Video updated successfully');
        } else {
            res.status(404).send('Video not found');
        }
    } catch (error) {
        res.status(500).send('Failed to update video');
    }
}

export async function deleteVideo(req, res) {
    try {
        const deletedVideo = await deleteVideoModel(req.params.id);
        if (deletedVideo) {
            res.send('Video deleted successfully');
        } else {
            res.status(404).send('Video not found');
        }
    } catch (error) {
        res.status(500).send('Failed to delete video');
    }
}

export async function fetchComments(req, res) {
    try {
        const videoId = req.params.videoId;  // Get video ID from request parameters
        const comments = await getCommentsByVideoId(videoId);
        res.json(comments);
    } catch (error) {
        res.status(500).send(error.message);
    }
}

export async function fetchCommentCount(req, res) {
    try {
        const videoId = req.params.videoId;  // Get video ID from request parameters
        const count = await countCommentsByVideoId(videoId);
        res.json({ count });
    } catch (error) {
        res.status(500).send(error.message);
    }
}

export async function fetchMixedVideos(req, res) {
    try {
        const videos = await getMixedVideos();
        res.json(videos);
    } catch (error) {
        res.status(500).send({ message: "Error fetching videos", error: error.message });
    }
}

export async function fetchVideosByCategory(req, res) {
    try {
        const category = req.params.category;  // Assumes category is passed as a URL parameter
        const videos = await getVideosByCategory(category);
        res.json(videos);
    } catch (error) {
        res.status(500).send({ message: "Error fetching videos by category", error: error.message });
    }
}



// import { getVideoModel, createVideoModel, getVideosModel } from '../services/videoPlay.js';

// export function getVideos(req, res) {
//     const videos = getVideosModel();
//     res.render('allVideos', { videos });
// }

// export function getVideo(req, res) {
//     const video = getVideoModel(req.params.id);
//     if (video) {
//         res.render('video', { video });
//     } else {
//         res.status(404).send('Video not found');
//     }
// }

// export function createVideo(req, res) {
//     createVideoModel(req.body.title, req.body.content);
//     res.redirect('/videoPlay');
// }