import express from 'express'
//import {login} from '../controllers/login.js'
import { checkUserNameAndPassword } from '../controllers/login.js';
const router = express.Router();

//router.post('/', login);
router.post('/', checkUserNameAndPassword);

export default router