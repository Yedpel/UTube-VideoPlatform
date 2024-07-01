import express from 'express'
//import { createToken } from '../controllers/tokens.js';
import { checkUserNameAndPassword } from '../controllers/login.js';
const router = express.Router();

//create token to authenticate user
//need to update the real adress, depand on how will be on client side//
// router.post('/', createToken);
router.post('/tokens', checkUserNameAndPassword);

export default router



