import { getVideoModel, createVideoModel, getVideosModel, updateVideoModel, deleteVideoModel,
     getVideosWithAuthorDetails } from '../services/videoPlay.js';

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