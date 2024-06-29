import express from 'express'
import { createToken } from '../controllers/tokens.js';
const router = express.Router();

//create token to authenticate user
//need to update the real adress, depand on how will be on client side//
router.post('/', createToken);


export default router



