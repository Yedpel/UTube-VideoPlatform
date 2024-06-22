import User from '../models/users.js';
import Video from '../models/videoPlay.js';

/**
 * Check if a username is already in use
 * @param {string} username - The username to check
 * @returns {Promise<boolean>} - Returns true if the username is in use, false otherwise
 */
/*
export const findUser = async (username) => {
    try {
        const user = await User.findOne({ userName: username, password: password });
        return !!user; // returns true if user is found, otherwise false
    } catch (err) {
        throw new Error('Error checking username and password');
    }
}; */
// Expected implementation in services/users.js
export const findUser = async (username) => {
    try {
        const user = await User.findOne({ username: username });
        return user != null;
    } catch (err) {
        console.error("Error checking username availability:", err);
        throw err;  // Ensure errors are thrown appropriately
    }
};

export async function updateUserModel(id, updateData) {
    return await User.findByIdAndUpdate(id, updateData, { new: true });
}


export async function deleteUserModel(id) {
    try {
        const user = await User.findByIdAndDelete(id);
        if (user) {
            await Video.deleteMany({ authorId: id });
        }
        return user;
    } catch (error) {
        throw error;
    }
}
/* export async function deleteUserModel(id) {
    return await User.findByIdAndDelete(id);
} */

// Using default export
export default User;

