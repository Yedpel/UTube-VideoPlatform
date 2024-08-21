import { sendWatchNotification } from '../services/cppServerService.js';

export const notifyVideoWatch = async (req, res) => {
    try {
        const { userId, videoId } = req.body;
        await sendWatchNotification(userId, videoId);
        res.status(200).json({ message: 'Notification sent to C++ server' });
    } catch (error) {
        console.error('Error notifying C++ server:', error);
        res.status(500).json({ message: 'Failed to notify C++ server' });
    }
};

export default notifyVideoWatch;
