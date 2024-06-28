import Video from '../models/videoPlay.js'; // Import the Mongoose model

// Fetch all!!!! videos from the database - just in case we need it
// we use mixed videos funciton down the page
export async function getVideosModel() {
    return await Video.find().populate('authorId', 'username profilePic'); 
}

// Function to get a video by its ID and populate the author details
export async function getVideoModel(id) {
    return await Video.findById(id).populate('authorId', 'username profilePic');
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

// Function to get all videos by a specific user and populate the author details
export async function getVideosbyUserId(userId) {
    return await Video.find({ authorId: userId }).populate('authorId', 'username profilePic');
}

export async function getMixedVideos() {
    try {
        // Fetch the top 10 viewed videos
        const topVideos = await Video.find().sort({ views: -1 }).limit(10).populate('authorId', 'username profilePic');

        // Get IDs of topVideos to exclude them from the random selection
        const topVideoIds = topVideos.map(video => video._id);

        // Fetch random videos excluding the top viewed ones
        const totalVideosCount = await Video.countDocuments();
        const randomVideosCount = Math.min(10, totalVideosCount - topVideos.length); // Ensure we don't fetch more than exists minus the top videos
        const randomVideos = await Video.aggregate([
            { $match: { _id: { $nin: topVideoIds } } }, // Exclude top viewed videos
            { $sample: { size: randomVideosCount } }
        ]);

        // Populate author details for random videos (since aggregate doesn't populate)
        const randomVideoIds = randomVideos.map(video => video._id);
        const populatedRandomVideos = await Video.find({ _id: { $in: randomVideoIds } }).populate('authorId', 'username profilePic');

        // Combine and shuffle the array
        const combinedVideos = [...topVideos, ...populatedRandomVideos];
        for (let i = combinedVideos.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [combinedVideos[i], combinedVideos[j]] = [combinedVideos[j], combinedVideos[i]]; // ES6 destructuring assignment for swapping
        }

        return combinedVideos;
    } catch (err) {
        console.error('Failed to fetch mixed videos:', err);
        throw err;
    }
}

export async function getVideosByCategory(category) {
    try {
        return await Video.find({ category: category }).populate('authorId', 'username profilePic');
    } catch (err) {
        console.error('Failed to fetch videos by category:', err);
        throw err;
    }
}

