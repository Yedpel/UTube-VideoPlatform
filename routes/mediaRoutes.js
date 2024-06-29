import express from 'express';
import multer from 'multer';
import fs from 'fs';
import path from 'path';

const router = express.Router();

const storage = multer.diskStorage({
    destination(req, file, cb) {
        cb(null, 'public/media/');
    },
    filename(req, file, cb) {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, file.fieldname + '-' + uniqueSuffix + path.extname(file.originalname));
    }
});

export const upload = multer({ storage: storage });

router.post('/upload', upload.single('file'), (req, res) => {
    res.json({ message: 'File uploaded successfully', filePath: `/media/${req.file.filename}` });
});


// just in case you want to replace a file actually
router.post('/replace', upload.single('file'), (req, res) => {
    const oldFilePath = req.body.oldFilePath; // Assuming the full path is provided
    fs.unlink(`public${oldFilePath}`, (err) => {
        if (err) {
            return res.status(500).json({ message: 'Failed to delete the old file' });
        }
        res.json({ message: 'File replaced successfully', newFilePath: `/media/${req.file.filename}` });
    });
});

export default router;
