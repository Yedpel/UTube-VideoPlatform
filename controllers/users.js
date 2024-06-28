// import { updateUserModel, deleteUserModel } from '../services/users.js';


// import User from '../services/users.js';
import * as userService from '../services/users.js';

/**
 * Handle user registration
 * @param {Request} req - Express request object
 * @param {Response} res - Express response object
 */
export const registerUser = async (req, res) => {
    const { firstName, lastName, date, email, profilePic, username, password } = req.body.newUser;
    try {
        // Check if the username is already taken
        const usernameExists = await userService.findUser(username);
        console.log(usernameExists);
        if (usernameExists) {
            return res.status(400).json({ message: 'Username is already in use' });
        }

        // console.log('hello')
        // Create a new user
        const newUser = { firstName, lastName, date, email, profilePic, username, password };
        userService.createUser({ newUser })
        // const user = new User({ firstName, lastName, date, email, profilePic, username, password });
        // console.log('hello')
        // await user.save();
        res.status(201).json({ message: 'User registered successfully' });
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

export const getUser =  async (req, res) => {
    // console.log(req.params.id);
    try {
        const user = await userService.getUserbyId(req.params.id);
        if (user !== null) {
            console.log('login successful the user is :' , user.username);
            console.log(user);
            res.json(user);
        } else {
            res.status(404).send('User not found');
            console.log('login not successful');
        }
    } catch (error) {
        console.log('login failed');
        res.status(500).send('Error fetching user');
    }
}


export async function updateUser(req, res) {
    try {
        const updatedUser = await userService.updateUserModel(req.params.id, req.body);
        if (updatedUser) {
            res.send('User updated successfully');
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to update user');
    }
}

export async function deleteUser(req, res) {
    try {
        const deletedUser = await userService.deleteUserModel(req.params.id);
        if (deletedUser) {
            res.send('User deleted successfully');
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to delete user');
    }
}
