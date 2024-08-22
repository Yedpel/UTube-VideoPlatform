#include <nlohmann/json.hpp>
#include <iostream>
#include <sstream>
#include <unistd.h>
#include <sys/socket.h>
#include <thread>
#include "user_thread_manager.hpp"
#include "video_manager.hpp"

using json = nlohmann::json;

void handleClient(int clientSocket)
{
    char buffer[4096];
    int bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);

    if (bytesRead <= 0)
    {
        std::cerr << "Error reading from socket or connection closed" << std::endl;
        return;
    }

    buffer[bytesRead] = '\0';
    std::cout << "Received: " << buffer << std::endl;

    // Extract JSON body from the received data
    std::string request(buffer);
    size_t pos = request.find("\r\n\r\n");
    std::string jsonBody;
    if (pos != std::string::npos)
    {
        jsonBody = request.substr(pos + 4);
    }
    else
    {
        jsonBody = request; // Assume the entire request is JSON if no headers are found
    }

    std::cout << "Extracted JSON body: " << jsonBody << std::endl;

    try
    {
        json data = json::parse(jsonBody);
        std::string action = data["action"];
        std::string userId = data["userId"];

        // Get current thread ID
        std::thread::id this_id = std::this_thread::get_id();
        std::stringstream ss;
        ss << this_id;
        std::string thread_id = ss.str();

        json response;
        response["threadId"] = thread_id;

        if (action == "create_thread")
        {
            UserThreadManager::getInstance().createThreadForUser(userId);
            response["message"] = "Thread created successfully";
            std::cout << "Thread " << thread_id << " created for user: " << userId << std::endl;
        }
        else if (action == "close_thread")
        {
            UserThreadManager::getInstance().closeThreadForUser(userId);
            response["message"] = "Thread closed successfully";
            std::cout << "Thread " << thread_id << " closed for user: " << userId << std::endl;
        }
        else if (action == "notify-watch")
        {
            if (!data.contains("videoId"))
            {
                throw std::runtime_error("Missing videoId for notify-watch action");
            }
            std::string videoId = data["videoId"];

            if (!UserThreadManager::getInstance().hasThreadForUser(userId))
            {
                UserThreadManager::getInstance().createThreadForUser(userId);
            }

            UserThreadManager::getInstance().processUserRequest(userId, action, videoId);
            response["message"] = "Video watch recorded";
            std::cout << "Thread " << thread_id << " - User " << userId << " watched video " << videoId << std::endl;
        }
        else if (action == "get_recommendations")
        {
            // TODO: Implement recommendation logic
            response["message"] = "Recommendations feature not implemented yet";
        }
        else
        {
            throw std::runtime_error("Unknown action: " + action);
        }

        std::string responseStr = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" + response.dump();
        send(clientSocket, responseStr.c_str(), responseStr.length(), 0);
    }
    catch (const json::exception &e)
    {
        std::cerr << "JSON parsing error: " << e.what() << std::endl;
        std::string response = "HTTP/1.1 400 Bad Request\r\nContent-Type: application/json\r\n\r\n";
        response += "{\"error\":\"Invalid JSON: " + std::string(e.what()) + "\"}";
        send(clientSocket, response.c_str(), response.length(), 0);
    }
    catch (const std::exception &e)
    {
        std::cerr << "Error processing request: " << e.what() << std::endl;
        std::string response = "HTTP/1.1 400 Bad Request\r\nContent-Type: application/json\r\n\r\n";
        response += "{\"error\":\"Invalid request: " + std::string(e.what()) + "\"}";
        send(clientSocket, response.c_str(), response.length(), 0);
    }

    close(clientSocket);
}