
THIS FILE is maintained in the source code, DO NOT modify the copy in the distribution!


Mutant Jukebox
==============

Normal/intended setup:
You have 
	1 Server(s) (networked desktop/laptop with storage of media. The media folders are shared)
		runs the mutant
		media is ripped DVDs (as ISO files for now)
		or other video files (.mpg, .avi etc)
	2 Player (networked desktop/laptop hooked up to the TV)
		runs the mutant and player software
		The mutant has no player software, it just runs windows commands.
	3 Remote (networked handheld/desktop/laptop)
		runs a browser
		connects to the mutant Player
		can browse the distributed library
		can remote play any movie on the Player.
		...so you can play all from the couch (and slowly turn into a potato or other vegetable)


FEATURES/purpose
---------------

1. Remote display&control of a media jukebox spread throughout the house.
	a. remote control from any browser, running on a WIFI device (palm, tablet etc)
	   YES, except play/pause/volume etc controls. Currently can only start playing certain file types
	b. view distributed library
	   YES for some types of movie files only
	c. play different file types
	   YES, see the user.xml file for supported types and player requiremetns
2. Easy maintenance and upgrade
	a. upgrade any computer in your network, remotely, to latest code/config
	   YES, one by one, no batch function
3. Supports series
	a. manually create a XXX.series.txt file in the folder with the episodes
	b. will list the series, distributed
	c. can see the contents of a series
	d. keeps track of played episodes from the series and which is next...
	e. it is independent of location (as long as all episodes are under the series folder)
	
Client certification: Windows/InternetBrowser, Nokia 770, Sony PSP
Server certification: Windows XP/Vista, JDK.1.6.0_14, Ubuntu 

Doesn't TVersity do it better? Yeah - i guess it does transcoding and it's more UPNP compliant. 
It also caches queries... However:
   1. it doesn't organize things nicely and automatically.
   2. it doesn't keep track of series easily
   3. it's cool, but it's not that cool :)


INSTALLATION
---------------

See http://wiki.homecloud.ca/installing, as well.

1. Install mutant (unzip) in folder c:\video\razmutant for now. (you can try another and let me know if it works :)
1.1. install jdk/jre 1.6.0_14 (or higher) and set JAVA_HOME env var...
1.2 alternatively, modify the mutant.cmd to set the JAVA_HOME right there, although it makes sense to have one set in OS

2. edit the agent.xml file, the clouds/cloud section with your hosts and network info, including location of media on each computer
2.1. ignore the proxy and other settings if you don't plan to open its port in the firewall (see SECURITY ISSUES below)

2.2. edit the media.xml file - the local media directories must be shared via windows and accessible

3. to play ISO files, install daemon tools and edit the path in user.xml
	make sure there is a default system player (WMP) for inserting movie DVDs, 
	otherwise the drive is mounted but the movie won't start

4. set the mutant.cmd as a startup thing in windows so it always runs 
	TIP: go to the shortcut's Properties and select "Run minimized".

5. TEST: connect "Remote" to "Player" using http://Player:4444/mutant
5.1. it's often faster to use IP instead of names. The mutant itself will replace name with IP in all it's absolute URLs


USER GUIDE
----------

Assuming it was installed and all mutants in the network are up and running.
	TIP: if setup as a "startup" program, it won't actually run until you login with that user!!!
	TIP: just drag and drop the mutant.cmd into the Start/All Applications/Startup folder

1. connect "Remote" to "Player" using http://Player:4444/mutant
	it's often faster to use IP instead of names. The mutant itself will replace name with IP in all it's absolute URLs

2. Validate install in network: from home page go to "network", i.e. http://localhost:4444/mutant/network/Network
	take the laptops/desktops one by one and try their "mutant" from here, make sure they're up
	ask their mutant's status

3. Upgrading
	upgrade the seed mutant manually and restart
	connect to it, browse its network and for each desktop/laptop select "updateTo"

4. view media
	go to Remote and connect to Player. Select "Media".
	select "Movies" either local (first icon) or all (search_movie icon)
	you'll see the list of known movie files from the Player device or all network devices (for search_movie)
	you can see "details" and go to google or IMDB
	if there's a file with the same name and extension ".jpg" or ".png" it will be displayed as both icon and details poster

4.1 play media from the displayed list
	select "play" and the file (either from Server or Player) will start playing on the Player.
	way some 4-5 sec, depending on file type
	if it doesn't work the first time, hit "refresh
	ignore the player buttons display when playing - they don't work!!! :D

5. stop mutant jukebox server
	unplug the box
	
6. find and save images for movies
	the mutant will use any image with the .jpg extension and the same name as the movie as 
	an icon for the movie.
	to find new icons, go the movie's "details" and choose "google".
	find the one you like (not too large since it will be downloaded all the time by client)
	copy it's URL (make sure it's not a screwy url, but a simply url pointing directly to the jpg)
	go back to the "details"
	select "saveJpg"
	do as it says: paste a & followed by the picture's url and hit enter

ADVANCED
--------
Presentation can be customized in user.xml - you can configure/script new pages as well
as code them in Java and configure them here.

ISSUES
------
If you have issues playing a certain kind of file, try to play it directly and see if it works...
if you identify a problem, let me know. - use the issue tracker in google code

TODO on Windows, some files with two consecutive spaces in their name don't wanna "play". Can repeat with starttrekvoyagerseries2x12.


ROADMAP/things to do
---------------

Email me more defects or enhancements...

1. add more movie file types
	DVD files/folders
1.1 use categories and category-based browsing
2. move inventory interface into sdk so folks can add new asset types
3. better protection
	use SSL with certificates etc
4. Self-upgrading so we don't have to use the freaky shell script (mutant.cmd)
5. setup as a windows service.
6. stream the remote lists as they're built, don't wait for all finders...its slow for many files...
7. player control


SECURITY ISSUES
---------------

The thing is designed to work on a secured internal network. It does not have a lot of protection from intruders/hackers.

Intruders can gain control of your computers and run anything on them.

The couch potato may be hazardous to your health. Please consult with a phisician for recommended usage and command line switches.


NETWORK ISSUES
--------------

The mutant keeps track of your network (all the mutants running inside the network). The network is defined in the agent.xml file: each computer (desktop/laptop) is assumed to have a mutant running. You also assign it an IP address.

The mutant will scan and udpate all IP addresses. It will also verify the identity of each remote node, so if the IP addresses are wrong, then the mutant will identify the correct peers.

TIP.1. If one of your computers is in a bad domain, it will not find the others through DNS lookups. The mutant bypasses that by looking up every IP address individually, pings the remote mutant and identifies that node.


RELEASE NOTES
---------------
0.1.5 - Oct '09
 too many changes to list
 plugins work...

0.1.4 - May'09
 lots of changes
 list of services with invokable methods
 scala
 split configuration in many files
 started actionables for player control - will integrate with VLC as the default player.
  
0.1.3
 added series
 added streams
 added sites
 added view type (small/list/large)
 optimized streaming html to client so there's less waiting when there are many assets
 automatically update IP addresses at startup and every 5 min
 many other fixes

0.1.2
 reorganized code
 player buttons work (volume, play/pause) if you use the standard windows media player
 
0.1.1
 reorganized the entire sdk to simplify "plugins"
 reorganized asset finders and their definition is now in user.xml
 added MPG movies
 fixed lower/upper case extension problem. Now i'll find both .iso and .ISO
 user.xml didn't change except for the new config/assets elements
 added a sample for finder/player for DVD folders - not implemented really
 added "SaveJpg" functionality on a movie's details, updated user guide below (6.)
 	now you can remotely find and save icons for the movies
 fixed Known defect: remote media jpgs are not displayed properly under "details"
 fixed Known defect: local/remote media jpgs are not displayed properly

0.1.0
 nicer pictures (thanks to Sorinel)
 bunch of fixes
 categories now work...
 user.xml change network/@active to network/@home, i.e. <network home="true"...
 user.xml change hosts/host/media/section removed. instead add multiple media tags with attribute category, see example 
 Known defect: remote media jpgs are not displayed properly under "details"
 Known defect: local/remote media jpgs are not displayed properly
 Known defect: when in remote network, local mutant links don't work well (i.e. "stop" uses the default ip)...
 known defect: CANNOT detect network if you're using the mutant on a computer which is VPN'd to work...it has a screwy ip
 known BIG DEFECT: only ISO files generally work fine with this...WMP responds kind'a whacky to other files...

0.0.4 - initial


SDK
===

The only thing you should have to do is to define new assets. 

A new asset type (i.e. "Music") implies an inventory, finders and players plus a lot of code. 
You better don't go there :)

You can add a new class of an already defined asset type. You only need to define a finder and a player.

1. If you need a new asset finder, extend the com.razie.sdk.AssetFinder. 
	It's best if you stick to files and hack folders somehow.

1.1. if you're just looking for files, use the standard finder with a different extension
	see examples in user.xml
	
2. To define a new player, implement com.razie.sdk.players.SdkPlayer. 
	Can extend one of the existing players.
	Player needs to be listed in config.xml and given a name which then can be used in 
	user.xml/config/assets/asset
	
3. Part of the player is the PlayerHandle, which will control the player from the remote.
	
	
Drawables
---------

When writing code that returns something, please use only Drawable. 
Do not format the html yourself. This will allow us to display on other interfaces in the future, 
if needed. It makes the code more organized and easier to write/understand anyways.


CODE INTRO
----------

Generally no code outside of the com.razie.sdk needs changed except to fix bugs.

Main class com.razie.comm.Mutant - should not need to be changed. It is basically a socket server.

Commands implement the CmdListener and are mostly in com.razie.comm.commands. 
The CmdGET is the web server.

Configuration is accessed via either MutantConfig (static config.xml in jar file) or UserConfig (user.xml). 
Use xpath all the time and don't cache stuff, to allow reloading config at runtime in the future. 
You will not be notified on reload...



