import mongoose from "mongoose";

const Schema = mongoose.Schema
const videoPlaySchema = new Schema({
    title: {
        type: String,
        required: true
    },
    category: {
        type: Option,
        required: true
    },
    Image: {
        type: String,
        required: true
    },
    video: {
        type: String,
        required: true
    },
    published: {
        type: Date,
        default: Date.now
    },
    views: {
        type: Number,
        default: 0
    },
    creator: {
        type: String,
        required: true
    },
});
export default mongoose.model('videoPlay', videoPlaySchema);