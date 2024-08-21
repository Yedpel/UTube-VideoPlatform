#ifndef VIDEO_MANAGER_HPP
#define VIDEO_MANAGER_HPP

#include <string>
#include <unordered_map>
#include <unordered_set>
#include <mutex>

class VideoManager
{
public:
    static VideoManager &getInstance();
    void addVideoView(const std::string &videoId, const std::string &userId);

private:
    VideoManager() = default;
    std::unordered_map<std::string, std::unordered_set<std::string>> videoViewers;
    std::mutex videoViewersMutex;
};

#endif // VIDEO_MANAGER_HPP