import express from 'express';
import { upload } from './mediaRoutes.js';
import { updateUser, deleteUser, registerUser, getUserSelectedDetails, getUserFullDetails } from '../controllers/users.js';
//import {isLoggedIn} from '../controllers/login.js';
import { isLoggedIn } from '../middlewares/auth.js';
import { checkUserNameAndPassword } from '../controllers/login.js';

const router = express.Router();

/*
// Route to create user
////are you sure that the route is correct?, isn't it need to be SignUp?////
router.post ('/SignUp', registerUser);
// router.post('/users',isLoggedIn, registerUser); */

// Route to create user with profile picture
router.post('/users', upload.single('profilePic'), registerUser);

// login route
// router.post('/login', checkUserNameAndPassword);

// Route to get a user details after authentication 
// router.get('/users/:id',isLoggedIn, getUser);

router.get('/users/:id',isLoggedIn, getUserSelectedDetails);

router.get('/users/:id/page', getUserFullDetails);



/*
// Route to update a user
router.put('/users/:id',isLoggedIn, updateUser);  */
// Route to update a user with new profile picture
router.put('/users/:id', isLoggedIn, upload.single('profilePic'), updateUser);


// Route to delete a user
router.delete('/users/:id',isLoggedIn, deleteUser);
// router.delete('/users/:id', isLoggedIn, deleteUser);


export default router;




