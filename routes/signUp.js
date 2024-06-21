import express from 'express'
// import {signUp} from '../controllers/signUp.js'
import { registerUser } from '../controllers/signUp.js';
const router = express.Router();

// router.post('/signUp', signUp);
router.post('/signup', registerUser);

export default router