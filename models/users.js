import mongoose from "mongoose";    
import bcrypt from "bcryptjs";

const Schema = mongoose.Schema 
// const newUserSchema = new Schema({
//     firstName: {
//         type: String,
//         required: true
//     },
//     lastName: {
//         type: String,
//         required: true
//     },
//     date: {
//         type: Date,
//         required: true
//     },
//     email: {
//         type: String,
//         required: true
//     },
//     profilePic:{
//         type: String,
//         required: true
//     },
//     userName: {
//         type: String,
//         required: true,
//         unique: true
//     },
//     password: {
//         type: String,
//         required: true
//     },
//     timestamp: {
//         type: Date,
//         default: Date.now
//     }
// });
const newUserSchema = new Schema({
    firstName: String,
    lastName: String,
    dob: String,  // Date of birth as String, change to Date if needed with proper format
    email: String,
    profilePic: String,
    username: {
        type: String,
        required: true,
        unique: true
    },
    password: String,
    timestamp: {
        type: Date,
        default: Date.now
    }
});


newUserSchema.pre('save', async function (next) {
    if (!this.isModified('password')) return next();

    try {
        const salt = await bcrypt.genSalt(10);
        this.password = await bcrypt.hash(this.password, salt);
        next();
    } catch (err) {
        next(err);
    }
});

// Password comparison method
newUserSchema.methods.comparePassword = function (candidatePassword) {
    return bcrypt.compare(candidatePassword, this.password);
};

// module.exports = mongoose.model('newUser', newUserSchema);
export default mongoose.model('User', newUserSchema);