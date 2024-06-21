import User from '../models/users.js';

const checkUserNameAndPassword = async (username, password) => {
    try {
        const user = await User.findOne({ userName: username, password: password });
        return !!user; // returns true if user is found, otherwise false
    } catch (err) {
        throw new Error('Error checking username and password');
    }
};

export default { checkUserNameAndPassword };
