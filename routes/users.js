import express from 'express';
import { updateUser, deleteUser,registerUser,getUser } from '../controllers/users.js';
import {isLoggedIn} from '../controllers/login.js';

const router = express.Router();

// Route to create user
router.post('/users',isLoggedIn, registerUser);  

// Route to get a user details after authentication 
router.get('/users/:id',isLoggedIn, getUser);  

// Route to update a user
router.put('/users/:id',isLoggedIn, updateUser);  

 // Route to delete a user
router.delete('/users/:id',isLoggedIn, deleteUser); 

export default router;




