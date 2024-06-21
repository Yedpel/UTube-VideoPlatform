import express from 'express';
import bodyParser from 'body-parser';
import routerVideoPlay from './routes/videoplay.js';
import routerSignUp from './routes/signUp.js';
import routerLogin from './routes/login.js';
import cors from 'cors';
import customEnv from 'custom-env';
import mongoose from 'mongoose';

//import session from 'express-session';
customEnv.env(process.env.NODE_ENV, './config');

mongoose.connect(process.env.CONNECTION_SRTING, { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => console.log('MongoDB connected'))
    .catch(err => console.error('MongoDB connection error:', err));

server.use(express.static('public'));
server.use(bodyParser.urlencoded({ extended: true }));
server.set('view engine', 'ejs');
server.set('views', './views');
server.use(cors());
server.use('/videoPlay', routerVideoPlay);
server.use('/login', routerLogin);
server.use('/signUp', routerSignUp);

const PORT = process.env.PORT || 89;
server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});