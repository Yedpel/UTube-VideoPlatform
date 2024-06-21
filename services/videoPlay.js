export function getVideosModel() {
    return videos;
}

export function getVideoModel(id){
    return videos.find(video => video.id === parseInt(id));
}

export function createVideoModel(title, content) {
    const lastvideo = videos[videos.length - 1];
    let newvideo = { id: lastvideo.id + 1, title, content };
    videos.push(newvideo);
}