#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <nlohmann/json.hpp>
#include <unordered_set>
#include <unordered_map>
#include <sstream>

using json = nlohmann::json;

const int SERVER_PORT = 5555;

std::unordered_map<std::string, std::unordered_set<std::string>> videoViewers;

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

    // Extract JSON body from HTTP request
    std::string request(buffer);
    std::istringstream iss(request);
    std::string line;
    std::string jsonBody;
    bool isBody = false;
    int contentLength = 0;

    while (std::getline(iss, line) && line != "\r")
    {
        if (line.find("Content-Length: ") == 0)
        {
            contentLength = std::stoi(line.substr(16));
        }
    }

    if (contentLength > 0)
    {
        jsonBody = request.substr(request.length() - contentLength);
    }

    std::cout << "Extracted JSON body: " << jsonBody << std::endl;

    try
    {
        json data = json::parse(jsonBody);
        std::string userId = data["userId"];
        std::string videoId = data["videoId"];

        videoViewers[videoId].insert(userId);
        //console the videoViewers
        for (auto const &x : videoViewers)
        {
            std::cout << "videoId: " << x.first << std::endl;
            for (auto const &y : x.second)
            {
                std::cout << "userId: " << y << std::endl;
            }
        }

        std::cout << "User " << userId << " watched video " << videoId << std::endl;

        // Send HTTP response
        std::string response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
        response += "{\"message\":\"Notification received\"}";
        send(clientSocket, response.c_str(), response.length(), 0);
    }
    catch (const std::exception &e)
    {
        std::cerr << "Error parsing JSON: " << e.what() << std::endl;
        // Send error response
        std::string response = "HTTP/1.1 400 Bad Request\r\nContent-Type: application/json\r\n\r\n";
        response += "{\"error\":\"Invalid JSON\"}";
        send(clientSocket, response.c_str(), response.length(), 0);
    }

    close(clientSocket);
}

int main()
{
    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket < 0)
    {
        std::cerr << "Error creating socket" << std::endl;
        return 1;
    }

    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(SERVER_PORT);

    if (bind(serverSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) < 0)
    {
        std::cerr << "Error binding socket" << std::endl;
        return 1;
    }

    if (listen(serverSocket, 5) < 0)
    {
        std::cerr << "Error listening on socket" << std::endl;
        return 1;
    }

    std::cout << "C++ server listening on port " << SERVER_PORT << std::endl;

    while (true)
    {
        sockaddr_in clientAddr;
        socklen_t clientAddrLen = sizeof(clientAddr);
        int clientSocket = accept(serverSocket, (struct sockaddr *)&clientAddr, &clientAddrLen);

        if (clientSocket < 0)
        {
            std::cerr << "Error accepting client connection" << std::endl;
            continue;
        }

        handleClient(clientSocket);
    }

    close(serverSocket);
    return 0;
}