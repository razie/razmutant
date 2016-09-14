    /**  ____    __    ____  ____  ____/___      ____  __  __  ____
     *  (  _ \  /__\  (_   )(_  _)( ___) __)    (  _ \(  )(  )(  _ \
     *   )   / /(__)\  / /_  _)(_  )__)\__ \     )___/ )(__)(  ) _ <
     *  (_)\_)(__)(__)(____)(____)(____)___/    (__)  (______)(____/
     *                      
     *  Copyright (c) Razvan Cojocaru, 2007+, Creative Commons Attribution 3.0
     */
     
Status: **not actively maintained**

Distributed agent cloud to manage and stream all media on your computers, remote automated upgrades, remote PC control, stream and play media from any device to any device with remote control. See http://homecloud.wikidot.com/ for diagrams and more details.

Complete and asynchronous http web server, asynchronous stream processing with filters and all in https://github.com/razie/razbase/tree/master/web

Agents: connected sets of distributed processes. See http://homecloud.wikidot.com/agents for samples and diagrams.
- can find each-other via UpNp and other procols inside a network
- can mutate: upgrade each-other
- can manage local assets remotely

Agents code in https://github.com/razie/razmutant/tree/master/agent/src/main/scala

A playground of ideas https://github.com/razie/razmutant/tree/master/play/src/main/scala/com/razie/playground

In-mem versioned and distributed sync in https://github.com/razie/razmutant/tree/master/mutant/src/main/scala/com/razie/dist/db

Assets - bastardized word, but here it is. You can think of them as **ManagedEntities** (see OSS/J) or something 
like stateful beans. Just like services are simple classes (one instance to serve all), the assets are 
individual objects which you can access remotely. They however have state and could be persisted.
They are developed much like the services (annotated methods).

There's some very basic multi-threading support (life), which keeps track of the threads and what they're doing.
I envision some beings jumping up and down inside the program, which need to breathe(). There's also workers which 
only process() something.

