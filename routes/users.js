import express from 'express';
import { updateUser, deleteUser } from '../controllers/users.js';

const router = express.Router();

// Assuming you have routes to get and create users already
router.put('/users/:id', updateUser);  // Route to update a user
router.delete('/users/:id', deleteUser);  // Route to delete a user

export default router;
