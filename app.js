import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors';
import fs from 'fs';
import mongoose from 'mongoose';
import customEnv from 'custom-env';

import routerVideoPlay from './routes/videoplay.js';
import routerSignUp from './routes/signUp.js';
import routerLogin from './routes/login.js';
import User from './models/users.js';
import Video from './models/videoPlay.js';
//add dotenv for environment variables
import dotenv from 'dotenv';
dotenv.config();

// Manually set the environment if NODE_ENV is not set
const env = process.env.NODE_ENV || 'local'; // Assuming 'local' is your development environment
customEnv.env(env, './config');
// Environment variables
//customEnv.env(process.env.NODE_ENV, './config');

// MongoDB connection
mongoose.connect(process.env.CONNECTION_STRING, { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => {
        console.log('MongoDB connected');
        loadData().then(() => {
            console.log('Data loaded successfully');
        });
    })
    .catch(err => console.error('MongoDB connection error:', err));

// Express app setup
const server = express();
server.use(express.static('public'));
server.use(bodyParser.urlencoded({ extended: true }));
server.use(cors());
server.set('view engine', 'ejs');
server.set('views', './views');

// Routes
server.use('/videoPlay', routerVideoPlay);
server.use('/login', routerLogin);
server.use('/signUp', routerSignUp);

// Load initial data into MongoDB
async function loadData() {
    try {
        // Read users and videos data from JSON files
        const usersData = JSON.parse(fs.readFileSync('./users.json', 'utf8'));
        const videosData = JSON.parse(fs.readFileSync('./videos.json', 'utf8'));

        // Clear existing data and load new data
        await User.deleteMany({});
        await User.insertMany(usersData);
        await Video.deleteMany({});
        await Video.insertMany(videosData);
    } catch (err) {
        console.error('Failed to load data:', err);
    }
}

// Start the server
const PORT = process.env.PORT || 89;
server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});



// import express from 'express';
// import bodyParser from 'body-parser';
// import routerVideoPlay from './routes/videoplay.js';
// import routerSignUp from './routes/signUp.js';
// import routerLogin from './routes/login.js';
// import cors from 'cors';
// import customEnv from 'custom-env';
// import mongoose from 'mongoose';

// //import session from 'express-session';
// customEnv.env(process.env.NODE_ENV, './config');

// mongoose.connect(process.env.CONNECTION_SRTING, { useNewUrlParser: true, useUnifiedTopology: true })
//     .then(() => console.log('MongoDB connected'))
//     .catch(err => console.error('MongoDB connection error:', err));

// server.use(express.static('public'));
// server.use(bodyParser.urlencoded({ extended: true }));
// server.set('view engine', 'ejs');
// server.set('views', './views');
// server.use(cors());
// server.use('/videoPlay', routerVideoPlay);
// server.use('/login', routerLogin);
// server.use('/signUp', routerSignUp);

// const PORT = process.env.PORT || 89;
// server.listen(PORT, () => {
//     console.log(`Server is running on port ${PORT}`);
// });