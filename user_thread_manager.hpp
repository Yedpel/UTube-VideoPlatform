#ifndef USER_THREAD_MANAGER_HPP
#define USER_THREAD_MANAGER_HPP

#include <string>
#include <unordered_map>
#include <thread>
#include <mutex>

class UserThreadManager
{
public:
    static UserThreadManager &getInstance();
    void createThreadForUser(const std::string &userId);
    void closeThreadForUser(const std::string& userId);

private:
    UserThreadManager() = default;
    std::unordered_map<std::string, std::thread> userThreads;
    std::mutex threadMapMutex;

    void handleUserRequests(const std::string &userId);
};

#endif // USER_THREAD_MANAGER_HPP