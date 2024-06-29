import jwt from 'jsonwebtoken';
//import userService from '../services/users.js';
import * as userService from '../services/users.js';
const key = "secretkey";  // Ensure this key is stored securely and is robust.

export async function isLoggedIn(req, res, next) {
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) {
        return res.status(401).json({ message: 'Token required' });
    }

    try {
        const decoded = jwt.verify(token, key);
        const user = await userService.getUserbyUsername(decoded.username);
        if (!user) {
            return res.status(401).json({ message: 'Unauthorized' });
        }

        req.user = user; // Add the user object to the request for downstream use
        next();
    } catch (err) {
        res.status(401).json({ message: 'Unauthorized, invalid token' });
    }
}
