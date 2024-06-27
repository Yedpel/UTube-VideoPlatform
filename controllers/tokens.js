import { getUserid } from '../services/users.js';
import jwt from 'jsonwebtoken';
const key = "secretkey";
import User from '../services/users.js';

// export const checkUserNameAndPassword = async (req, res) => {
//     const { username, password } = req.body;
//     try {
//         const User = await userService.findUser(username, password);
//         // console.log(`User found: ${User}`);
//         if (User) {
//             res.status(200).json({ message: 'Login successful', user: User });
//         } else {
//             res.status(401).json({ message: 'Invalid username or password' });
//         }
//     } catch (err) {
//         res.status(500).json({ message: 'Internal server error' });
//     }
// };


export const createToken = async (req, res) => {
    const { username, password } = req.body;
    try {
        // console.log(req.body);
        // console.log(username);
        // console.log(password);
        // console.log(req);
        const id = await getUserid(username, password);
        // console.log(`User found: ${user}`); 
        if (id) {
            console.log(id);
            // Generate JWT token
            const token = jwt.sign(username, key);
            res.status(200).json({
                message: 'Login successful',
                token: token,
                userId: id
            });
        } else {
            res.status(404).json({ message: 'Invalid username or password' });
        }
    } catch (err) {
        console.error('Error during login:', err);
        res.status(500).json({ message: 'Internal server error' });
    }
};






// export function isLoggedIn(req, res, next) {
//     if (req.session.username != null)
//         return next()
//     else
//         res.redirect('/login')
// }