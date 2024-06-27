import express from 'express';
import { updateUser, deleteUser,registerUser,getUser } from '../controllers/users.js';

const router = express.Router();

// Route to create user
router.post('/', registerUser);  

// Route to get a user
router.get('/:id', getUser);  

// Route to update a user
router.put('/:id', updateUser);  

 // Route to delete a user
router.delete('/:id', deleteUser); 

export default router;
