import express from 'express';
import { notifyVideoWatch } from '../controllers/cppServerController.js';

const router = express.Router();

router.post('/notify-watch', notifyVideoWatch);

export default router;