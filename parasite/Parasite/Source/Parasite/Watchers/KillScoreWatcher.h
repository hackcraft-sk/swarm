#pragma once

#include <string>

#include "Parasite/Watchers/AchievementWatcher.h"

namespace BWAPI
{
	class Game;
}

namespace Parasite
{
	class AchievementsCollector;
}

namespace Parasite_Watchers
{
	class KillScoreWatcher : public AchievementWatcher
	{
		private:
			BWAPI::Game &game;

			int scoreGapLimit;
			std::string achievementName;
		public:
			KillScoreWatcher(Parasite::AchievementsCollector &achievementsCollector, BWAPI::Game &game, int scoreGapLimit, std::string achievementName);

			void checkEndScore();
	};
}
