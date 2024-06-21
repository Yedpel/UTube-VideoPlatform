import userService from '../services/users.js'

export const checkUserNameAndPassword = async (req, res) => {
    const { username, password } = req.body;

    try {
        const user = await userService.findUser(username, password);
        if (user) {
            res.status(200).json({ message: 'Login successful' });
        } else {
            res.status(401).json({ message: 'Invalid username or password' });
        }
    } catch (err) {
        res.status(500).json({ message: 'Internal server error' });
    }
};

export function isLoggedIn(req, res, next) {
    if (req.session.username != null)
        return next()
    else
        res.redirect('/login')
}