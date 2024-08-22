import { sendWatchNotification, createThreadForUser, closeThreadForUser } from '../services/cppServerService.js';

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
