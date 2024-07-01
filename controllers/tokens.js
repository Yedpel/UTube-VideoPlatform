import { getUserid } from '../services/users.js';
import jwt from 'jsonwebtoken';
const key = "secretkey";

export const createToken = async (req, res) => {
    const { username} = req.body;
    try {
        const id = await getUserid(username);
        if (id) {
           // console.log(id);
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


// export async function isLoggedIn(req, res, next) {
//     if (req.headers.authorization) {
//         let token = req.headers.authorization.split(' ')[1]
//         try {
//             if (token.at(0) === '"') {
//                 token = token.slice(1, -1)
//             }
//             const decoded = jwt.verify(token, key)
//             const [username, password] = decoded.data.split(';');
//             const user = await userService.findUser(username);
//             if (user && user.password === password) {
//                 req.user = user.username;
//                 next();
//             } else {
//                 res.status(401).json({ message: 'Unauthorized' });
//             }
//         }
//         catch (err) {
//             res.status(401).json({ message: 'Unauthorized' });
//         }
//     } else {
//         res.status(401).json({ message: 'Token required' });
//     }

// }



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
