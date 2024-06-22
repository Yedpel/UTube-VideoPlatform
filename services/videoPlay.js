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

export async function likeVideo(videoId, userId) {
    return await Video.findByIdAndUpdate(
        videoId,
        { 
            $inc: { likes: 1 },
            $addToSet: { likedBy: userId }  // Ensures the user ID is only added once
        },
        { new: true }
    );
}

export async function unlikeVideo(videoId, userId) {
    return await Video.findByIdAndUpdate(
        videoId,
        { 
            $inc: { likes: -1 },
            $pull: { likedBy: userId }  // Removes the user ID from the array
        },
        { new: true }
    );
}

export async function isUserLikedVideo(videoId, userId) {
    const video = await Video.findById(videoId);
    if (!video) {
        throw new Error('Video not found');
    }
    return video.likedBy.includes(userId);
}

// Function to get videos with editable author details that showed - username and profilePic
export async function getVideosWithAuthorDetails() {
    return await Video.find()
                      .populate({
                          path: 'authorId',
                          select: 'username profilePic -_id'  // Only fetch the username and profilePic
                      });
}

// Function to check if a user is the author of a video
export async function isUserTheAuthor(videoId, userId) {
    const video = await Video.findById(videoId);
    if (!video) {
        throw new Error('Video not found');
    }
    return video.authorId.toString() === userId;
}

// Function to get all videos by a specific user
export async function getVideosByUserId(userId) {
    return await Video.find({ authorId: userId });
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