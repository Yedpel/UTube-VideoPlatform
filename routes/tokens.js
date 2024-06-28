import express from 'express'
import { createToken } from '../controllers/tokens.js';
const router = express.Router();

//create token to authenticate user
router.post('/', createToken);


export default router



