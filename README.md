SWARM
=====

*StarCraft War Automatic Relaunching Machines* is an automatic system
for launching SC: BW games developed for purposes of StarCraft Micro AI Tournaments,
but in core is not bound to any particular game and can be adjusted for 
launching cooperated game environments.

# Modules contained in SWARM

* **BWTV**, Overlay for streaming information about matches.
* **Cerebrate**, Application coordinating multiple game environments, usually
	connected to web server, but can be coordinated any other way.
* **JWebComm**, Communication subsystem
* **Overlord**, Application handling one particular game environment instance (i.e. StarCraft game instance)
* **Overmind**, Web application developed particulary for StarCraft Micro AI Tournaments,
	used for match planning, bot uploads, ladders, communication with *Cerebrate*, etc.
* **Parasite**, Module injected to every StarCraft match for statistics collection.
