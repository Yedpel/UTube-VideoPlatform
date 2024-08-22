import net from 'net';

const CPP_SERVER_HOST = 'localhost';
const CPP_SERVER_PORT = 5555;

function sendRequestToCppServer(action, data) {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();

        client.connect(CPP_SERVER_PORT, CPP_SERVER_HOST, () => {
            console.log('Connected to C++ server');
            const message = JSON.stringify({ action, ...data });
            const request = `POST / HTTP/1.1\r\nContent-Type: application/json\r\nContent-Length: ${message.length}\r\n\r\n${message}`;
            client.write(request);
        });

        client.on('data', (data) => {
            console.log('Received from C++ server:', data.toString());
            const response = data.toString();
            const jsonStartIndex = response.indexOf('{');
            const jsonEndIndex = response.lastIndexOf('}');
            if (jsonStartIndex !== -1 && jsonEndIndex !== -1) {
                const jsonBody = response.slice(jsonStartIndex, jsonEndIndex + 1);
                client.destroy();
                resolve(JSON.parse(jsonBody));
            } else {
                client.destroy();
                resolve(response);
            }
        });

        client.on('close', () => {
            console.log('Connection closed');
        });

        client.on('error', (err) => {
            console.error('Connection error:', err);
            reject(err);
        });
    });
}

export const createThreadForUser = (userId) => {
    return sendRequestToCppServer('create_thread', { userId });
};

export const closeThreadForUser = (userId) => {
    return sendRequestToCppServer('close_thread', { userId });
};

export const sendWatchNotification = (userId, videoId) => {
    return sendRequestToCppServer('notify-watch', { userId, videoId });
};