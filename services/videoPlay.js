import Video from '../models/videoPlay.js'; // Import the Mongoose model

export async function getVideosModel() {
    return await Video.find(); // Fetch all videos from the database
}

export async function getVideoModel(id){
    return await Video.findById(id); // Find a video by its MongoDB ObjectId
}

export async function createVideoModel(videoData) {
    const video = new Video(videoData); // Create a new video instance with the passed data
    return await video.save(); // Save the new video to the database
}

export async function updateVideoModel(id, updateData) {
    return await Video.findByIdAndUpdate(id, updateData, { new: true }); // Returns the updated document
}

export async function deleteVideoModel(id) {
    return await Video.findByIdAndDelete(id); // Deletes the video by its ID
}



// export function getVideosModel() {
//     return videos;
// }

// export function getVideoModel(id){
//     return videos.find(video => video.id === parseInt(id));
// }

// export function createVideoModel(title, content) {
//     const lastvideo = videos[videos.length - 1];
//     let newvideo = { id: lastvideo.id + 1, title, content };
//     videos.push(newvideo);
// }