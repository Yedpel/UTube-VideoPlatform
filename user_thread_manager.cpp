#include "user_thread_manager.hpp"
#include "video_manager.hpp"
#include <iostream>
#include <thread>
#include <sstream>

UserThreadManager &UserThreadManager::getInstance()
{
    static UserThreadManager instance;
    return instance;
}

void UserThreadManager::createThreadForUser(const std::string &userId)
{
    std::lock_guard<std::mutex> lock(threadMapMutex);
    if (userThreads.find(userId) == userThreads.end())
    {
        userThreadActive[userId] = true;
        userThreads[userId] = std::thread(&UserThreadManager::handleUserRequests, this, userId);
        std::cout << "Thread created for user: " << userId << std::endl;
    }
}

void UserThreadManager::closeThreadForUser(const std::string &userId)
{
    std::lock_guard<std::mutex> lock(threadMapMutex);
    auto it = userThreads.find(userId);
    if (it != userThreads.end())
    {
        userThreadActive[userId] = false;
        userCondVars[userId].notify_one();
        if (it->second.joinable())
        {
            it->second.join();
        }
        userThreads.erase(it);
        userMessageQueues.erase(userId);
        userMutexes.erase(userId);
        userCondVars.erase(userId);
        userThreadActive.erase(userId);
        std::cout << "Thread closed for user: " << userId << std::endl;
    }
}

bool UserThreadManager::hasThreadForUser(const std::string &userId)
{
    std::lock_guard<std::mutex> lock(threadMapMutex);
    return userThreads.find(userId) != userThreads.end();
}

void UserThreadManager::processUserRequest(const std::string &userId, const std::string &action, const std::string &videoId)
{
    std::lock_guard<std::mutex> lock(userMutexes[userId]);
    userMessageQueues[userId].push({action, videoId});
    userCondVars[userId].notify_one();
}

void UserThreadManager::handleUserRequests(const std::string &userId)
{
    std::thread::id this_id = std::this_thread::get_id();
    std::stringstream ss;
    ss << this_id;
    std::string thread_id = ss.str();

    std::cout << "Thread " << thread_id << " started for user: " << userId << std::endl;

    while (userThreadActive[userId])
    {
        std::unique_lock<std::mutex> lock(userMutexes[userId]);
        userCondVars[userId].wait(lock, [this, &userId]
                                  { return !userMessageQueues[userId].empty() || !userThreadActive[userId]; });

        if (!userThreadActive[userId])
            break;

        auto message = userMessageQueues[userId].front();
        userMessageQueues[userId].pop();
        lock.unlock();

        if (message.action == "notify-watch")
        {
            VideoManager::getInstance().addVideoView(message.videoId, userId);
            std::cout << "Thread " << thread_id << " - User " << userId << " watched video " << message.videoId << std::endl;
        }
        // Add other actions here as needed
    }

    std::cout << "Thread " << thread_id << " ended for user: " << userId << std::endl;
}