// import { updateUserModel, deleteUserModel } from '../services/users.js';


// import User from '../services/users.js';
import * as userService from '../services/users.js';
import jwt from 'jsonwebtoken';
const key = "secretkey"; // Ensure this key is stored securely and consistently


export const registerUser = async (req, res) => {
    const { firstName, lastName, date, email, username, password } = req.body;
    const profilePic = req.file ? `/media/${req.file.filename}` : '';

    try {
        const usernameExists = await userService.findUser(username);
        if (usernameExists) {
            return res.status(400).json({ message: 'Username is already in use' });
        }

        const newUser = { firstName, lastName, date, email, profilePic, username, password };
        const createdUser = await userService.createUser(newUser);
        res.status(201).json({ message: 'User registered successfully' });
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};
/*
export const registerUser = async (req, res) => {
    //const { firstName, lastName, date, email, profilePic, username, password } = req.body.newUser;
    const { firstName, lastName, date, email, profilePic, username, password } = req.body.newUser || req.body;
    try {
        // Check if the username is already taken
        const usernameExists = await userService.findUser(username);
        if (usernameExists) {
            return res.status(400).json({ message: 'Username is already in use' });
        }

        // Create a new user
        const newUser = { firstName, lastName, date, email, profilePic, username, password };
       // userService.createUser({ newUser })
       const createdUser = await userService.createUser(newUser); // Pass newUser directly
        res.status(201).json({ message: 'User registered successfully' });
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};
*/

export const getUser = async (req, res) => {
    // console.log(req.params.id)
    ;
    try {
        const user = await userService.getUserbyId(req.params.id);
        if (user !== null) {
            console.log('login successful the user is :', user.username);
            console.log(user);
            res.json(user);
        } else {
            res.status(404).send('User not found');
            console.log('login not successful');
        }
    } catch (error) {
        console.log('login failed');
        res.status(500).send('Error fetching user');
    }
}

export async function deleteUser(req, res) {
    try {
        const deletedUser = await userService.deleteUserModel(req.params.id);
        if (deletedUser) {
            res.send('User deleted successfully');
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to delete user');
    }
}


export async function updateUser(req, res) {
    try {
        if (req.body.username && req.body.username !== req.user.username) {
            const usernameExists = await userService.findUser(req.body.username);
            if (usernameExists) {
                return res.status(400).json({ message: 'Username is already in use' });
            }
        }

        const updateData = { ...req.body };
        if (req.file) {
            updateData.profilePic = `/media/${req.file.filename}`;
        }

        const updatedUser = await userService.updateUserModel(req.params.id, updateData);
        if (updatedUser) {
            // Issue a new token if username was part of the update
            if (req.body.username) {
                const newToken = jwt.sign({ username: updatedUser.username }, key, { expiresIn: '5h' });
                res.json({ message: 'User updated successfully', token: newToken });
            } else {
                res.send('User updated successfully');
            }
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to update user');
    }
}

///////////client side code to handle username update for token///////////
/*
const handleSubmit = async (e) => {
  e.preventDefault();
  const formData = new FormData();
  formData.append('firstName', newUser.firstName);
  formData.append('lastName', newUser.lastName);
  formData.append('username', newUser.username);  // Ensure this matches the server's expected fields
  formData.append('email', newUser.email);  // Assuming you want to update email as well
  if (imageFile) {
    formData.append('profilePic', imageFile);
  }

  fetch(`http://localhost:12345/api/users/${userLoggedIn.id}`, {
    method: 'PUT',
    body: formData,
    headers: {
      'Authorization': `Bearer ${userLoggedIn.token}`
    }
  })
  .then(response => response.json())
  .then(data => {
    console.log(data);
    if (data.token) {
      localStorage.setItem('token', data.token); // Update the token in local storage
      // Potentially update the token in your app's state or context
    }
    alert('User updated successfully'); // Feedback to user
  })
  .catch(error => {
    console.error('Error:', error);
    setError('Update failed. Please try again.');
  });
};

*/
////end of client side code to handle picture changeand username update for token///

//// old code for updating user details in client side with token - without multer////
/*
// Example of handling the response after updating user details
fetch('/api/users/update', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${oldToken}` // Your existing token
    },
    body: JSON.stringify(userData)
})
.then(response => response.json())
.then(data => {
    if (data.token) {
        localStorage.setItem('token', data.token); // Save the new token
        // Update the global headers or request instance used for API calls
        api.setToken(data.token);
    }
    console.log(data.message);
})
.catch(error => console.error('Error:', error));
*/



/*
export async function updateUser(req, res) {
    try {
        //if the username changed, check if the new username is already in use,
        //if the username is not changed, no need to check
        if (req.body.username) {
            const usernameExists = await userService.findUser(req.body.username);
            if (usernameExists) {
                return res.status(400).json({ message: 'Username is already in use' });
            }
        }
      
        const updatedUser = await userService.updateUserModel(req.params.id, req.body);
        if (updatedUser) {
            res.send('User updated successfully');
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to update user');
    }
} */


    ///before merge multer and token code/////

    
//             res.json({ message: 'User updated successfully' });
//         } else {
//             res.status(404).send('User not found');
//         }
//     } catch (error) {
//         res.status(500).send('Failed to update user');
//     }
// }

/*
// update user - if username is changed, check if it is already taken, and issue a new token
export async function updateUser(req, res) {
    try {
        // Check if new username is already taken
        if (req.body.username && req.body.username !== req.user.username) {
            const usernameExists = await userService.findUser(req.body.username);
            if (usernameExists) {
                return res.status(400).json({ message: 'Username is already in use' });
            }
        }

        const updatedUser = await userService.updateUserModel(req.params.id, req.body);
        if (updatedUser) {
            // Issue a new token if username was part of the update
            if (req.body.username) {
                const newToken = jwt.sign({ username: updatedUser.username }, key, { expiresIn: '5h' });
                res.json({ message: 'User updated successfully', token: newToken });
            } else {
                res.send('User updated successfully');
            }
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to update user');
    }
}

*/