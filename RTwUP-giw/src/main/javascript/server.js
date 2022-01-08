/**
 * Node.js server script Required node packages: express, redis, socket.io
 */
const PORT = 8000;
const HOST = 'localhost';
const REDIS_PORT = 6379;
const REDIS_HOST = 'localhost';
const CHANNEL = 'RTwUP';

const express = require('express');
const http = require('http');
const path = require('path');
const redis = require('redis');

const app = express();

const server = http.createServer(app);

const io = require("socket.io")(server, {
  cors: {
    origin: "*"
  }
});

app.use(express.static('public'));

// Serves all the request which includes /images in the url from Images folder
app.use('/images', express.static(__dirname + '/images'));

app.get('/', function (req, res) {
  res.sendFile(path.resolve('index.html'));
});

if (!module.parent) {
	server.listen(PORT, HOST, function () {
		log('info', "Server in ascolto all'indirizzo: http://"+ HOST + ":" + PORT);
	  
		const socket = io.listen(server);
	    socket.on('connection', function(client) {
	    	log("info", "Listening IO");
	    });

        const client = redis.createClient({
            host: REDIS_HOST,
            port: REDIS_PORT
        });
    
        client.connect();
    
        const subscriber = client.duplicate();
    
        subscriber.on('error', (err) => log('error', 'Redis Client Error'));
    
        subscriber.on('connect', () => {
            log('info', 'Connected to Redis server.');
            log('info', 'Subscribing to ' + CHANNEL);
            subscriber.subscribe(CHANNEL, (message) => {
              log('msg', "Received from channel "+ CHANNEL + ": " + message);
              socket.send(message);
            });
        });
        
        subscriber.on('disconnect', () => {
            log('warn', 'Disconnetting from Redis.');
            subscriber.quit();
        });
    
        subscriber.connect();
    })
}


function log(type, msg) {

    var color = '\u001b[0m';
        reset = '\u001b[0m';

    switch(type) {
        case "info":
            color = '\u001b[36m';
            break;
        case "warn":
            color = '\u001b[33m';
            break;
        case "error":
            color = '\u001b[31m';
            break;
        case "msg":
            color = '\u001b[34m';
            break;
        default:
            color = '\u001b[0m';
    };

    console.log(color + '   ' + type + '  - ' + reset + msg);
}
