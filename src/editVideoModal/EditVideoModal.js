import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
// import './EditVideoModal.css';

function EditVideoModal({ video, closeModal }) {

  const Navigate = useNavigate();
  const token = sessionStorage.getItem('token');
  console.log('Video:', video);
  const [formData, setFormData] = useState({
    thumbnailUrl: video.thumbnailUrl,
    videoUrl: video.videoUrl,
    title: video.title,
    authorId: video.authorId,
    author: video.author,
    category: video.category,
  });

  const [previewThumbnail, setPreviewThumbnail] = useState(null);
  const [previewVideo, setPreviewVideo] = useState(null);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (video.thumbnailUrl) {
      setPreviewThumbnail(video.thumbnailUrl);
    }
  }, [video]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    const allowedImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif'];

    if (file && allowedImageTypes.includes(file.type)) {
      setFormData({ ...formData, thumbnailUrl: file });
      setPreviewThumbnail(URL.createObjectURL(file));
      setErrors({ ...errors, thumbnailUrl: '' });
    } else {
      setFormData({ ...formData, thumbnailUrl: video.thumbnailUrl });
      setPreviewThumbnail(video.thumbnailUrl);
      setErrors({ ...errors, thumbnailUrl: 'Please select a valid image file (JPEG, JPG, PNG, WebP, or GIF).' });
    }
  };

  const handleVideoChange = (e) => {
    const file = e.target.files[0];
    const allowedVideoTypes = ['video/mp4', 'video/webm', 'video/ogg'];

    if (file && allowedVideoTypes.includes(file.type)) {
      setFormData({ ...formData, videoUrl: file });
      setPreviewVideo(URL.createObjectURL(file));
      setErrors({ ...errors, videoUrl: '' });
    } else {
      setFormData({ ...formData, videoUrl: video.videoUrl });
      setPreviewVideo(video.videoUrl);
      setErrors({ ...errors, videoUrl: 'Please select a valid video file (MP4, WebM, or OGG).' });
    }
  };

  const handleSubmit = async () => {
    const newErrors = {};

    if (formData.title !== video.title) {
      if (!formData.title) {
        newErrors.title = 'Please enter a title.';
      }
    }

    if (formData.category !== video.category) {
      if (!formData.category) {
        newErrors.category = 'Please select a category.';
      }
    }

    if (formData.thumbnailUrl !== video.thumbnailUrl) {
      if (!formData.thumbnailUrl) {
        newErrors.thumbnailUrl = 'Please upload an image.';
      }
    }

    if (formData.videoUrl !== video.videoUrl) {
      if (!formData.videoUrl) {
        newErrors.videoUrl = 'Please upload a video.';
      }
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      const formDataToUpload = new FormData();
      formDataToUpload.append('thumbnail', formData.thumbnailUrl);
      formDataToUpload.append('video', formData.videoUrl); // Add this line
      formDataToUpload.append('title', formData.title);
      formDataToUpload.append('category', formData.category);

      const response = await fetch(`http://localhost:12345/api/users/${video.authorId}/videos/${video._id}`, {
        method: 'PUT',
        body: formDataToUpload,
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const updatedVideo = await response.json();
        console.log('Updated video:', updatedVideo);
        // setVideosList([...videosList, newVideo]);
        console.log('token after',response.token  );
        window.location.reload();
        // Navigate('/');
        // setUpdatedList(true);
        closeModal();
        // window.location.reload('/');
      } else {
        console.error('Failed to update video');
      }
    } catch (error) {
      console.error('Failed to update video:', error);
    }
  };

  return (
    <div className="modal fade show" id="editVideoModal" tabIndex="-1" aria-labelledby="editVideoModalLabel" aria-hidden="true" style={{ display: 'block' }}>
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="editVideoModalLabel">Edit Video</h1>
            <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={closeModal}></button>
          </div>
          <div className="modal-body" style={{ maxHeight: '70vh', overflowY: 'auto' }}>
            {previewThumbnail && (
              <div className="mb-3">
                <label htmlFor="videoThumbnail" className="form-label me-3 video-prev">Image Preview:</label>
                <img src={previewThumbnail} alt="Current video thumbnail" className="current-video-img" style={{ maxWidth: '100%', height: '200px' }} />
              </div>
            )}
            {previewVideo && (
              <div className="mb-3">
                <label htmlFor="currentVideo" className="form-label video-prev">Video Preview:</label>
                <video controls src={previewVideo} className="current-video-clip" style={{ maxWidth: '80%', height: '300px' }}></video>
              </div>
            )}
            <div className="mb-3">
              <label htmlFor="videoTitle" className="col-form-label">Video Title:</label>
              <input type="text" className="form-control" id="videoTitle" name="title" value={formData.title} onChange={handleChange} />
              {errors.title && <div className="text-danger">{errors.title}</div>}
            </div>
            <div className="mb-3">
              <label htmlFor="videoCategory" className="col-form-label">Category:</label>
              <select className="form-select" id="videoCategory" name="category" value={formData.category} onChange={handleChange}>
                <option value="">Select a category</option>
                <option value="Sport">Sport</option>
                <option value="News">News</option>
                <option value="Cinema">Cinema</option>
                <option value="Gaming">Gaming</option>
              </select>
              {errors.category && <div className="text-danger">{errors.category}</div>}
            </div>
            <div className="mb-3">
              <label htmlFor="videoThumbnail" className="col-form-label">Image:</label>
              <input type="file" className="form-control" name="thumbnailUrl" id="videoThumbnail" onChange={handleImageChange} />
              {errors.thumbnailUrl && <div className="text-danger">{errors.thumbnailUrl}</div>}
            </div>
            <div className="mb-3">
              <label htmlFor="videoVideoUrl" className="col-form-label">Video:</label>
              <input type="file" className="form-control" name="videoUrl" id="videoVideoUrl" onChange={handleVideoChange} />
              {errors.videoUrl && <div className="text-danger">{errors.videoUrl}</div>}
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary btn-ops" data-bs-dismiss="modal" onClick={closeModal}>
              <h5 className="ops mb-0">Close</h5>
            </button>
            <button type="button" className="btn btn-primary btn-ops" onClick={handleSubmit}>
              <h5 className="ops mb-0">Save changes</h5>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditVideoModal;