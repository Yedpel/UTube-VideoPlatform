import User from '../models/users.js';


export const checkUserNameAndPassword = async (username, password) => {
    try {
        console.log(`Checking login for username: ${username} and password: ${password}`);
        const user = await User.findOne({ username: username, password: password });
        console.log(`User found: ${user}`);
        return !!user;
    } catch (err) {
        console.error('Failed to find user:', err);
        throw new Error('Error checking username and password');
    }
};

/*export const checkUserNameAndPassword = async (username, password) => {
    try {
        const user = await User.findOne({ username: username, password: password });
        return !!user; // returns true if user is found, otherwise false
    } catch (err) {
        throw new Error('Error checking username and password');
    }
};*/

//export default { checkUserNameAndPassword };
