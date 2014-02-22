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
	class FastGameWatcher : public AchievementWatcher
	{
		private:
			BWAPI::Game &game;

			int secondsLimit;
			std::string achievementName;
		public:
			FastGameWatcher(Parasite::AchievementsCollector &achievementsCollector, BWAPI::Game &game, int secondsLimit, std::string achievementName);

			void checkEndTime();
	};
}
