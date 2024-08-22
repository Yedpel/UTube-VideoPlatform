import {
    sendWatchNotification, createThreadForUser, closeThreadForUser, getRecommendations,
    getAllVideosWithViewCounts, 
} from '../services/cppServerService.js';
import jwt from 'jsonwebtoken';
import User from '../models/users.js';

const key = "secretkey";


export const getVideoRecommendations = async (req, res) => {
    try {
        const { videoId, token } = req.body;
        let userId = 'guest';

        if (!videoId) {
            return res.status(400).json({ message: 'videoId is required' });
        }

        if (token && token !== 'guest') {
            try {
                const decoded = jwt.verify(token, key);
                const user = await User.findOne({ username: decoded.username });
                if (user) {
                    userId = user._id.toString();
                }
            } catch (error) {
                console.error('Error verifying token:', error);
                // If token verification fails, we'll use 'guest' as userId
            }
        }

        // Get all videos with their view counts
        const allVideos = await getAllVideosWithViewCounts();

        const recommendationsWithDetails = await getRecommendations(userId, videoId, allVideos);

        res.status(200).json({
            message: 'Recommendations retrieved successfully',
            recommendations: recommendationsWithDetails
        });
    } catch (error) {
        console.error('Error getting video recommendations:', error);
        res.status(500).json({ message: 'Failed to get video recommendations' });
    }
};

export const notifyVideoWatch = async (req, res) => {
    try {
        const userId = req.user.id; // Assuming the user ID is stored in req.user after authentication
        const { videoId } = req.body;

        if (!videoId) {
            return res.status(400).json({ message: 'videoId is required' });
        }

        const result = await sendWatchNotification(userId, videoId);
        res.status(200).json({ message: 'Watch notification sent successfully', result });
    } catch (error) {
        console.error('Error notifying video watch:', error);
        res.status(500).json({ message: 'Failed to notify video watch' });
    }
};

export const createUserThread = async (req, res) => {
    try {
        const userId = req.user.id; // Assuming the user ID is stored in req.user after authentication
        const result = await createThreadForUser(userId);
        res.status(200).json({ message: 'Thread created successfully', result });
    } catch (error) {
        console.error('Error creating thread in C++ server:', error);
        res.status(500).json({ message: 'Failed to create thread in C++ server' });
    }
};

export const closeUserThread = async (req, res) => {
    try {
        const userId = req.user.id; // Assuming the user ID is stored in req.user after authentication
        const result = await closeThreadForUser(userId);
        res.status(200).json({ message: 'Thread closed successfully', result });
    } catch (error) {
        console.error('Error closing thread in C++ server:', error);
        res.status(500).json({ message: 'Failed to close thread in C++ server' });
    }
};

export default notifyVideoWatch;
