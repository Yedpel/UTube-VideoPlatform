import User from '../models/users.js';

/**
 * Check if a username is already in use
 * @param {string} username - The username to check
 * @returns {Promise<boolean>} - Returns true if the username is in use, false otherwise
 */
export const findUser = async (username) => {
    try {
        const user = await User.findOne({ userName: username, password: password });
        return !!user; // returns true if user is found, otherwise false
    } catch (err) {
        throw new Error('Error checking username and password');
    }
};
