**RTwUP**

**Realtime Twitter Url Popularity**
Given a suitably filtered stream of documents returned by a Twitter query, calculate real-time statistics and show the ranking of the most tweeted URLs since system activation.
The statistics must be updated on screen every N seconds.  
They show the links organized into various domain categories, each with its counting popularity:  

| Domain | Link | Frequency |  
| :----: | :--: | :-------: |  
| foursquare.com | expanded.url.com/123 | 9 times |  
| foursquare.com | expanded.url.com/456 |8 times |  
| youtube.com | ... | ... |  
| instagram.com | ... | ...|   
...  

**Data Stream Description and Requirements**
The system has to use Twitter APIs ([Twitter4j][02], [Hosebird][03] for instance) to perform queries and retrieve Tweets, suitably filters them (e.g. according to the coordinates of a polygon centered on Rome, Milan or a city of your choice).  
The links of interest are the ones retrieved from the entities/urls field of the Tweet json: 
* first of all, links have to be expanded, reversing the output of Twitter's shortening service (*t.co*);
* if the Tweet contains the expanded form of the URL, the count is assigned to it;
* if the Tweet contains a “shortened” form of the URL (e.g. bit.ly/13NHE7v , goo.gl/uJH2Y , http://instagr.am/p/S3l5rQjCcA/, etc ...), then it has to be expanded in order to obtain the completely expanded form (eventually after several expansions); the count can then be assigned to it.
 
Starting from the final expanded form, domain information can be extracted to organize the current results.  
This must be done in real time, using [Storm][01].

**Adopted Technologies**
RTwUP is developed in *Java*.  
To listen to Twitter's stream, it was chosen [Twitter4j][02], *Twitter Stream API* in particular.  
To process the Tweets real time, it was chosen [Storm][01].  
The user interface is written as a [Node.js][04] application, making use of [socket.io][05] and [Redis][06] to display results in real time.  

For more information, you can refer to the wiki pages.

**Wiki**

* [Setting up the Maven project][07]
* [Creation of a Twitter account to use][08]
* [Installing and setting up Node.js][09]
* [Setting up Redis][10]
* [How to start RTwUP][11]



[01]: https://storm.apache.org "Apache Storm"

[02]: http://twitter4j.org/en/ "Twitter APIs in Java"

[03]: https://github.com/twitter/hbc "Hosebird client"

[04]: http://nodejs.org/ "Node.js web page"

[05]: http://socket.io/ "socket.io web page"

[06]: http://redis.io/ "Redis web page"

[07]: https://github.com/Dani7B/RTwUP-giw/wiki/Setting-up-the-Maven-project "Setting up the Maven project"

[08]: https://github.com/Dani7B/RTwUP-giw/wiki/Creation-of-a-Twitter-account-to-use "Creation of a Twitter account to use"

[09]: https://github.com/Dani7B/RTwUP-giw/wiki/Installing-and-setting-up-Node.js "Installing and setting up Node.js"

[10]: https://github.com/Dani7B/RTwUP-giw/wiki/Setting-up-Redis "Setting up Redis"

[11]: https://github.com/Dani7B/RTwUP-giw/wiki/How-to-start-RTwUP "How to start RTwUP"
