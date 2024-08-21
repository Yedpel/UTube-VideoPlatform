#include "user_thread_manager.hpp"
#include <iostream>

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
        userThreads[userId] = std::thread(&UserThreadManager::handleUserRequests, this, userId);
        userThreads[userId].detach();
    }
}

void UserThreadManager::closeThreadForUser(const std::string &userId)
{
    std::lock_guard<std::mutex> lock(threadMapMutex);
    auto it = userThreads.find(userId);
    if (it != userThreads.end())
    {
        // In a real-world scenario, you'd want to signal the thread to stop
        // Here we'll just remove it from the map
        userThreads.erase(it);
        std::cout << "Thread closed for user: " << userId << std::endl;
    }
}

void UserThreadManager::handleUserRequests(const std::string &userId)
{
    std::cout << "Thread created for user: " << userId << std::endl;
    // TODO: Implement logic to handle user requests
    // This function will run in a separate thread for each user
}