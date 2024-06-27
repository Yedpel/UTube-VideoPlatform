import express from 'express'
import { createToken } from '../controllers/tokens.js';
const router = express.Router();

//router.post('/', login);
router.post('/', createToken);
// router.get('/', checkUser);


export default router