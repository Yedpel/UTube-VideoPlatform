#include "video_manager.hpp"

VideoManager &VideoManager::getInstance()
{
    static VideoManager instance;
    return instance;
}

void VideoManager::addVideoView(const std::string &videoId, const std::string &userId)
{
    std::lock_guard<std::mutex> lock(videoViewersMutex);
    videoViewers[videoId].insert(userId);
}