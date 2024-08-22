#ifndef USER_THREAD_MANAGER_HPP
#define USER_THREAD_MANAGER_HPP

#include <string>
#include <unordered_map>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <queue>
#include <atomic>

struct UserMessage
{
    std::string action;
    std::string videoId; // Only used for notify-watch
};

class UserThreadManager
{
public:
    static UserThreadManager &getInstance();
    void createThreadForUser(const std::string &userId);
    void closeThreadForUser(const std::string &userId);
    bool hasThreadForUser(const std::string &userId);
    void processUserRequest(const std::string &userId, const std::string &action, const std::string &videoId = "");

private:
    UserThreadManager() = default;
    std::unordered_map<std::string, std::thread> userThreads;
    std::unordered_map<std::string, std::queue<UserMessage>> userMessageQueues;
    std::unordered_map<std::string, std::mutex> userMutexes;
    std::unordered_map<std::string, std::condition_variable> userCondVars;
    std::unordered_map<std::string, std::atomic<bool>> userThreadActive;
    std::mutex threadMapMutex;

    void handleUserRequests(const std::string &userId);
};

#endif