import { updateUserModel, deleteUserModel } from '../services/users.js';

export async function updateUser(req, res) {
    try {
        const updatedUser = await updateUserModel(req.params.id, req.body);
        if (updatedUser) {
            res.send('User updated successfully');
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to update user');
    }
}

export async function deleteUser(req, res) {
    try {
        const deletedUser = await deleteUserModel(req.params.id);
        if (deletedUser) {
            res.send('User deleted successfully');
        } else {
            res.status(404).send('User not found');
        }
    } catch (error) {
        res.status(500).send('Failed to delete user');
    }
}
