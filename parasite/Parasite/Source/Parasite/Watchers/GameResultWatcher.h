#pragma once

#include "Parasite/Watchers/AchievementWatcher.h"

namespace Parasite
{
	class AchievementsCollector;
}

namespace Parasite_Watchers
{
	class GameResultWatcher : public AchievementWatcher
	{
		public:
			GameResultWatcher(Parasite::AchievementsCollector &achievementsCollector);

			void setBotGameResult(bool wins);
	};
}
