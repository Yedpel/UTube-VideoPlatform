import mongoose from "mongoose";

const Schema = mongoose.Schema


const videoPlaySchema = new Schema({
    //id is automatically created by mongoDB if not provided
    id: String,
    thumbnailUrl: String,
    title: String,
    author : String,
    authorPofilePic: String,
    views: Number,
    uploadTime :{
        type: String,
        default: Date.now
    } ,
    videoUrl: String,
    category: String,
    likes: Number,
    likedBy: Array,
    comments: Array, 

});



export default mongoose.model('videoPlay', videoPlaySchema);

  // old names of the fields in videoPlaySchema ://
    // published: {
    //     type: Date,
    //     default: Date.now
    // },
    //uplaodTime string, if ont exist, use default Date.now as string
    
   // creator: String,

// tyuta for comments schema//
// const commentSchema = new Schema({
//     username: String,
//     text: String,
//     uploadTime: {
//         type: String,
//         default: Date.now
//     },
//     likes: Number,
//     likedByUsers: Array,
//     profilePicUrl: String
// });


//old schema for videoPlay//
// const videoPlaySchema = new Schema({
//     title: {
//         type: String,
//         required: true
//     },
//     category: {
//         type: Option,
//         required: true
//     },
//     Image: {
//         type: String,
//         required: true
//     },
//     video: {
//         type: String,
//         required: true
//     },
//     published: {
//         type: Date,
//         default: Date.now
//     },
//     views: {
//         type: Number,
//         default: 0
//     },
//     creator: {
//         type: String,
//         required: true
//     },
//     Comments: {
//         type: Array,
//         default: []
//     }
// });