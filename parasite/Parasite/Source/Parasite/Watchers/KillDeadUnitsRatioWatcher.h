#pragma once

#include <string>

#include "Parasite/Watchers/AchievementWatcher.h"

namespace BWAPI
{
	class Player;
}

namespace Parasite
{
	class AchievementsCollector;
}

namespace Parasite_Watchers
{
	class KillDeadUnitsRatioWatcher : public AchievementWatcher
	{
		private:
			BWAPI::Player &player;
			int killRatio;
			int deadRatio;

			std::string achievementName;
		public:
			KillDeadUnitsRatioWatcher(Parasite::AchievementsCollector &achievementsCollector, BWAPI::Player &player, int killRatio, int deadRatio, std::string achievementName);

			void checkKillDeadUnitsRatio();
	};
}
