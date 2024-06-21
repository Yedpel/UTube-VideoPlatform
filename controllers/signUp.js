import User from '../services/users.js';
import { findUser } from '../services/users.js';

/**
 * Handle user registration
 * @param {Request} req - Express request object
 * @param {Response} res - Express response object
 */
export const registerUser = async (req, res) => {
    const { firstName, lastName, date, email, profilePic, userName, password, passwordConfirm } = req.body;

    if (password !== passwordConfirm) {
        return res.status(400).json({ message: "Passwords do not match" });
    }

    try {
        // Check if the username is already taken
        const usernameExists = await findUser(userName);
        if (usernameExists) {
            return res.status(400).json({ message: 'Username is already in use' });
        }

        const user = new User({ firstName, lastName, date, email, profilePic, userName, password });
        await user.save();
        res.status(201).json({ message: 'User registered successfully' });
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};
