#include "video_manager.hpp"
#include <iostream> 

VideoManager &VideoManager::getInstance()
{
    static VideoManager instance;
    return instance;
}

void VideoManager::addVideoView(const std::string &videoId, const std::string &userId)
{
    std::lock_guard<std::mutex> lock(videoViewersMutex);
    if (!videoExists(videoId))
    {
        createVideo(videoId);
    }
    videoViewers[videoId].insert(userId);
    std::cout << "User " << userId << " watched video " << videoId << std::endl;
}

bool VideoManager::videoExists(const std::string &videoId)
{
    return videoViewers.find(videoId) != videoViewers.end();
}

void VideoManager::createVideo(const std::string &videoId)
{
    videoViewers[videoId] = std::unordered_set<std::string>();
    std::cout << "Created new video entry for " << videoId << std::endl;
}