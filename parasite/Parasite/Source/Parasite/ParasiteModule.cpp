#include "Parasite/ParasiteModule.h"

#include <set>
#include <map>
#include <sstream>

#include <cstdlib>

#include <BWAPI.h>

#include "BWU/Core/Log.h"

#include "Parasite/Settings.h"
#include "Parasite/AutoCamera.h"
#include "Parasite/OverlordConnection.h"
#include "Parasite/PlayerColor.h"
#include "Parasite/AchievementsCollector.h"
#include "Parasite/MessageInterface.h"

#include "Parasite/Watchers/GameResultWatcher.h"
#include "Parasite/Watchers/FastGameWatcher.h"

using std::set;
using std::map;
using std::stringstream;
using std::string;
using std::make_pair;
using namespace BWAPI;
using BWU::Log;
using Parasite_Watchers::GameResultWatcher;
using Parasite_Watchers::FastGameWatcher;

namespace Parasite
{
	ParasiteModule::ParasiteModule(Game &game, Log &log, OverlordConnection &overlordConnection)
	: game(game)
	, log(log)
	, overlordConnection(overlordConnection)
	, autocam(AutoCamera(&game))
	{
		gameResultWatcher = new GameResultWatcher(gameWatcher);
		winUnder1MinutesWatcher = new FastGameWatcher(gameWatcher, game, 60, "winUnder1Minute");
		winUnder3MinutesWatcher = new FastGameWatcher(gameWatcher, game, 60 * 3, "winUnder3Minutes");
		defetUnder1MinutesWatcher = new FastGameWatcher(gameWatcher, game, 60, "defeatUnder1Minute");
	}

	void ParasiteModule::onStart()
	{
		overlordConnection.open();

		Color playerGameColor = game.self()->getColor();

		PlayerColor playerColor(playerGameColor);
		overlordConnection.sendPlayerColor(playerColor);

		// enable user input
		// only works when using this as normal AI module
		game.enableFlag(Flag::UserInput);

		// set the command optimization level (reduces high APM, size of bloated replays, etc)
		game.setCommandOptimizationLevel(Settings::MINIMUM_COMMAND_OPTIMIZATION);

		// watchable, but quite fast speed
		game.setLocalSpeed(20);

		// we want nice view :)
		game.setFrameSkip(0);
	}

	void ParasiteModule::onEnd(bool isWinner)
	{
		gameResultWatcher->setBotGameResult(isWinner);

		if (isWinner)
		{
			winUnder1MinutesWatcher->checkEndTime();
			winUnder3MinutesWatcher->checkEndTime();
		}
		else
		{
			defetUnder1MinutesWatcher->checkEndTime();
		}

		set<Achievement*> earnedAchievements = gameWatcher.getEarnedAchievements();
		overlordConnection.sendAchievements(earnedAchievements);

		Player &self = *game.self();
		map<string, int> scores;
		scores.insert(make_pair("kill", self.getKillScore()));
		overlordConnection.sendScores(scores);

		overlordConnection.close();
	}

	void ParasiteModule::onFrame()
	{
		autocam.update();

		if(game.getFrameCount() % 30 == 0)
		{
			overlordConnection.sendAlivePing();
		}

		drawPlayers();

		int y = 50;
		set<Player*> players = game.getPlayers();
		set<Player*>::iterator it;
		for (it = players.begin(); it != players.end(); it++)
		{
			Player *player = *it;

			game.drawTextScreen(10, y, "%d", player->getKillScore());

			y += 20;
		}
	}

	void ParasiteModule::drawPlayers()
	{
		int x = 180;
		int y = 320;
		int offset = 17;

		std::set<Player*> players = game.getPlayers();
		std::set<Player*>::iterator it;
		for (it = players.begin(); it != players.end(); it++)
		{
			Player *player = *it;

			if (player->getType() != PlayerTypes::Player)
			{
				continue;
			}

			drawPlayerInfo(x, y, player->getName(), player->getColor());

			y += offset;
		}
	}
	
	void ParasiteModule::drawPlayerInfo(int x, int y, std::string name, Color color)
	{
		int squareSize = 13;

		int bLeft, bTop, bRight, bBottom;
		bLeft = x - 2;
		bTop = y - 2;
		bRight = x + 100 + 2;
		bBottom = y + squareSize + 2;

		game.drawBoxScreen(bLeft, bTop, bRight, bBottom, Colors::Black, true);
		game.drawBoxScreen(x, y, x + squareSize, y + squareSize, color, true);
		game.drawTextScreen(x + 17, y, "%s", name.c_str());
	}

	void ParasiteModule::onSendText(std::string text)
	{
	}

	void ParasiteModule::onReceiveText(Player* player, std::string text)
	{
	}

	void ParasiteModule::onPlayerLeft(Player* player)
	{
	}

	void ParasiteModule::onPlayerDropped(Player* player)
	{
	}

	void ParasiteModule::onNukeDetect(Position target)
	{
	}

	void ParasiteModule::onUnitDiscover(Unit* unit)
	{
	}

	void ParasiteModule::onUnitEvade(Unit* unit)
	{
	}

	void ParasiteModule::onUnitShow(Unit* unit)
	{
	}

	void ParasiteModule::onUnitHide(Unit* unit)
	{
	}

	void ParasiteModule::onUnitCreate(Unit* unit)
	{
	}

	void ParasiteModule::onUnitDestroy(Unit* unit)
	{
	}

	void ParasiteModule::onUnitMorph(Unit* unit)
	{
	}

	void ParasiteModule::onUnitComplete(Unit *unit)
	{
	}

	void ParasiteModule::onUnitRenegade(Unit* unit)
	{
	}

	void ParasiteModule::onSaveGame(std::string gameName)
	{
	}
}
