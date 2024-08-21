#include "request_handler.hpp"
#include "user_thread_manager.hpp"
#include "video_manager.hpp"
#include <nlohmann/json.hpp>
#include <iostream>
#include <sstream>
#include <unistd.h>
#include <sys/socket.h>

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

        if (action == "create_thread")
        {
            std::string userId = data["userId"];
            UserThreadManager::getInstance().createThreadForUser(userId);

            // Send HTTP response
            std::string response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
            response += "{\"message\":\"Thread created successfully\"}";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else if (action == "close_thread")
        {
            std::string userId = data["userId"];
            UserThreadManager::getInstance().closeThreadForUser(userId);

            // Send HTTP response
            std::string response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
            response += "{\"message\":\"Thread closed successfully\"}";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else if (action == "watch_video")
        {
            std::string userId = data["userId"];
            std::string videoId = data["videoId"];
            VideoManager::getInstance().addVideoView(videoId, userId);
            std::cout << "User " << userId << " watched video " << videoId << std::endl;

            // Send HTTP response
            std::string response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
            response += "{\"message\":\"Video watch recorded\"}";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else
        {
            throw std::runtime_error("Unknown action");
        }
    }
    catch (const std::exception &e)
    {
        std::cerr << "Error processing request: " << e.what() << std::endl;
        // Send error response
        std::string response = "HTTP/1.1 400 Bad Request\r\nContent-Type: application/json\r\n\r\n";
        response += "{\"error\":\"Invalid request: " + std::string(e.what()) + "\"}";
        send(clientSocket, response.c_str(), response.length(), 0);
    }

    close(clientSocket);
}