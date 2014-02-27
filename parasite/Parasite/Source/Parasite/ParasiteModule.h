#pragma once

#include <BWAPI.h>
#include <string>

#include "Parasite/AutoCamera.h"

#include "Parasite/AchievementsCollector.h"

namespace BWU
{
	class Log;
}

namespace Parasite_Watchers
{
	class GameResultWatcher;
	class FastGameWatcher;
	class KillScoreWatcher;
}

namespace Parasite
{
	class OverlordConnection;

	class ParasiteModule: public BWAPI::AIModule
	{
		private:
			BWAPI::Game &game;
			Parasite::AutoCamera autocam;

			BWU::Log &log;

			OverlordConnection &overlordConnection;

			void drawPlayers();
			void drawPlayerInfo(int x, int y, std::string name, BWAPI::Color color);

			// achievement watchers
			AchievementsCollector gameWatcher;

			Parasite_Watchers::GameResultWatcher *gameResultWatcher;
			Parasite_Watchers::FastGameWatcher *winUnder1MinutesWatcher;
			Parasite_Watchers::FastGameWatcher *winUnder3MinutesWatcher;
			Parasite_Watchers::FastGameWatcher *defetUnder1MinutesWatcher;
			Parasite_Watchers::KillScoreWatcher *killDominanceWatcher;
		public:
			ParasiteModule(BWAPI::Game &game, BWU::Log &log, OverlordConnection &overlordConnection);
			virtual void onStart();
			virtual void onEnd(bool isWinner);
			virtual void onFrame();
			virtual void onSendText(std::string text);
			virtual void onReceiveText(BWAPI::Player* player, std::string text);
			virtual void onPlayerLeft(BWAPI::Player* player);
			virtual void onNukeDetect(BWAPI::Position target);
			virtual void onUnitDiscover(BWAPI::Unit* unit);
			virtual void onUnitEvade(BWAPI::Unit* unit);
			virtual void onUnitShow(BWAPI::Unit* unit);
			virtual void onUnitHide(BWAPI::Unit* unit);
			virtual void onUnitCreate(BWAPI::Unit* unit);
			virtual void onUnitDestroy(BWAPI::Unit* unit);
			virtual void onUnitMorph(BWAPI::Unit* unit);
			virtual void onUnitRenegade(BWAPI::Unit* unit);
			virtual void onSaveGame(std::string gameName);
			virtual void onUnitComplete(BWAPI::Unit *unit);
			virtual void onPlayerDropped(BWAPI::Player* player);
	};
}
