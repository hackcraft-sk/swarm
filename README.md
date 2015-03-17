SWARM
=====

*StarCraft War Automatic Relaunching Machines* is an automatic system
for launching SC: BW games developed for purposes of StarCraft Micro AI Tournaments,
but in core is not bound to any particular game and can be adjusted for 
launching cooperated game environments.

# Modules contained in SWARM

* **BWTV**, Video capture module with integrated display of overlay informations and streaming to twitch.tv
* **Cerebrate**, Application coordinating game sessions (managed by *Overlord*s). Their parameters are usually provided by web server, but this can be changed to other ways.
* **JWebComm**, Communication message-based abstraction subsystem, designed for using with different backends (http, plain tcp)
* **Overlord**, Application handling one particular game environment instance (i.e. StarCraft game instance). Usually coordinated by *Cerebrate*.
* **Overmind**, Web application developed particulary for StarCraft Micro AI Tournaments,
	used for match planning, bot uploads, ladders, communication with *Cerebrate*, etc.
* **Parasite**, Module injected to every StarCraft match for communication with *Cerebrate*. Used for providing informations about match state and statistics data collection.
