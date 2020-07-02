/**
 * Node.js server script Required node packages: express, redis, socket.io
 */
const PORT = 8000;
const HOST = 'localhost';
const REDIS_PORT = 6379;
const REDIS_HOST = 'localhost';

const express = require('express');
const http = require('http');
const path = require('path');
const redis = require('redis');
const io = require('socket.io');

const app = express();

const server = http.createServer(app);

app.use(express.static('public'));

// Serves all the request which includes /images in the url from Images folder
app.use('/images', express.static(__dirname + '/Images'));

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
		
		const subscriber = redis.createClient(REDIS_PORT,REDIS_HOST);
		 
		subscriber.on("connect", function() {
			log('info', 'Connected to Redis server.');
	        subscriber.subscribe('RTwUP');
	        log('info', 'Subscribed to RTwUP.');
		});
		
		subscriber.on("message", function(channel, message) {
            socket.send(message);
            log('msg', "Received from channel "+ channel+ ": "+ message);
        });
		
		subscriber.on('disconnect', function() {
	        log('warn', 'Disconnetting from Redis.');
	    	subscriber.quit();
	    });
		
	});
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
};
