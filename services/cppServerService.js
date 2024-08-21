import net from 'net';

const CPP_SERVER_HOST = '127.0.0.1';
const CPP_SERVER_PORT = 5555;

export const sendWatchNotification = (userId, videoId) => {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();

        client.connect(CPP_SERVER_PORT, CPP_SERVER_HOST, () => {
            console.log('Connected to C++ server');
            const message = JSON.stringify({ userId, videoId });
            client.write(message);
        });

        client.on('data', (data) => {
            console.log('Received from C++ server:', data.toString());
            client.destroy();
            resolve();
        });

        client.on('close', () => {
            console.log('Connection closed');
        });

        client.on('error', (err) => {
            console.error('Connection error:', err);
            reject(err);
        });
    });
};

export default sendWatchNotification;