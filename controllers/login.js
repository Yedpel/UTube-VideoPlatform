//import userService from '../services/users.js'
import * as userService from '../services/users.js';
import jwt from 'jsonwebtoken'
const key = "secretkey"
import bcrypt from 'bcryptjs';


export const checkUserNameAndPassword = async (req, res) => {
    const { username, password } = req.body;
  
    try {
     //   const user = await userService.findUser(username);
        const user = await userService.getUserbyUsername(username);
        const match = password===user.password;

        if (user &&  match) { 
            const token = jwt.sign({ username }, key, { expiresIn: '5h' });

            res.status(200).json({ message: 'Login successful', token, userId: user.id});
        } else {
            console.log('Invalid username or password');
            res.status(401).json({ message: 'Invalid username or password' });
        }
    } catch (err) {
        console.error('Login error:', err.message);  // This will print more specific error info to the console.
        res.status(500).json({ message: 'Internal server error' });
    }
};


// export const checkUserNameAndPassword = async (req, res) => {
//     const { username, password } = req.body;
//     try {
//         const user = await userService.findUser(username);
//         if (user && user.password === password) {
//             const token = jwt.sign({ data: `${username};${password}` }, key);

//             res.status(200).json({ message: 'Login successful', token });
//         } else {
//             res.status(401).json({ message: 'Invalid username or password' });
//         }
//     } catch (err) {
//         res.status(500).json({ message: 'Internal server error' });
//     }
// };

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



//     // if (req.session.username != null)
//     //     return next()
//     // else
//     //     res.redirect('/login')
// }