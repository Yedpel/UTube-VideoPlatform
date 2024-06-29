import User from '../models/users.js';
import Video from '../models/videoPlay.js';
import Comment from '../models/comments.js';


export const createUser = async (newUser) => {
    try {
        const user = new User(newUser);
        await user.save();
        return user; // It's often useful to return the created user object
    } catch (error) {
        throw new Error('Error creating user: ' + error.message);
    }
};


// export const createUser = async (newUser) => {
//     try {
//         const user = new User(newUser);
//         await user.save();
//     } catch (error) {
//         throw new Error('Error creating user: ' + error.message);
//     }

// }

export const findUser = async (username) => {
    try {
        const user = await User.findOne({ username: username });
        return user != null;
    } catch (err) {
        console.error("Error checking username availability:", err);
        throw err;  // Ensure errors are thrown appropriately
    }
};
///why you added password here ?  just check if the username is already in use or not///
/*
export const findUser = async (username, password) => {
    try {
        const user = await User.findOne({ username: username, password: password });
        return !!user; // returns true if user is found, otherwise false
    } catch (err) {
        console.error("Error finding user:", err);
        throw err;  // Ensure errors are thrown appropriately
    }
}; */

export const getUserid = async (username) => {
    try {
        const user = await User.findOne({ username: username });
        return user._id;
    }
    catch (error) {
        throw new Error('Error fetching user: ' + error.message);
        throw error;
    }

}

// export const getUserbyNameAndPass = async (username, password) => {
//     try {
//         const user = await User.findOne({ username: username, password: password });
//         return user;
//     }
//     catch (error) {
//         throw new Error('Error fetching user: ' + error.message);
//         throw error;
//     }
// }


export const getUserbyId = async (id) => {
    // console.log('hello');
    try {
        const user = await User.findById({ _id: id });
        console.log(user);
        if (user === null) {
            console.log('User not found');
            throw new Error('User not found');
        } else {
            console.log('User found 1');
            const userObj = 
                 {
                    firstName: user.firstName,
                    lastName: user.lastName,
                    profilePic: user.profilePic,
                    username: user.username,
                    password: user.password,
                    email: user.email,
                    date: user.date,
                    id: user._id
                }
            console.log(userObj);
            return userObj;
        }
    } catch (error) {
        throw new Error('Error fetching user: ' + error.message);
    }
}

//get user by username just like the above function
export const getUserbyUsername = async (username) => {
    try {
        const user = await User.findOne({ username: username });
        console.log(user);
        if (user === null) {
            console.log('User not found');
            throw new Error('User not found');
        } else {
            console.log('User found 1');
            const userObj = 
                 {
                    firstName: user.firstName,
                    lastName: user.lastName,
                    profilePic: user.profilePic,
                    username: user.username,
                    password: user.password,
                    email: user.email,
                    date: user.date,
                    id: user._id
                }
            console.log(userObj);
            return userObj;
        }
    } catch (error) {
        throw new Error('Error fetching user: ' + error.message);
    }
}

export async function updateUserModel(id, updateData) {
    return await User.findByIdAndUpdate(id, updateData, { new: true });
}


export async function deleteUserModel(id) {
    try {
        const user = await User.findByIdAndDelete(id);
        if (!user) {
            throw new Error('User not found');
        }

        // Fetch and delete all comments made by the user
        const comments = await Comment.find({ userId: id });
        const commentIds = comments.map(comment => comment._id);

        // Remove these comments from the videos
        await Video.updateMany(
            {},
            { $pull: { comments: { $in: commentIds } } }
        );

        // Delete all comments made by the user
        await Comment.deleteMany({ userId: id });

        // Delete videos authored by the user
        await Video.deleteMany({ authorId: id });

        return user;
    } catch (error) {
        throw new Error('Error deleting user and related data: ' + error.message);
    }
}



// Using default export
export default User;




/*
 * Check if a username is already in use
// * @param {string} username - The username to check
// * @returns {Promise<boolean>} - Returns true if the username is in use, false otherwise
 */
/*
export const findUser = async (username) => {
    try {
        const user = await User.findOne({ userName: username, password: password });
        return !!user; // returns true if user is found, otherwise false
    } catch (err) {
        throw new Error('Error checking username and password');
    }
}; */
// Expected implementation in services/users.js