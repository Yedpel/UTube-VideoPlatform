import { getVideoModel, createVideoModel, getVideosModel } from '../services/videoPlay';

export function getVideos(req, res) {
    const videos = getVideosModel();
    res.render('allVideos', { videos });
}

export function getVideo(req, res) {
    const video = getVideoModel(req.params.id);
    if (video) {
        res.render('video', { video });
    } else {
        res.status(404).send('Video not found');
    }
}

export function createVideo(req, res) {
    createVideoModel(req.body.title, req.body.content);
    res.redirect('/videoPlay');
}