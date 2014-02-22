#pragma once

#include <set>

#include "Parasite/Achievement.h"

namespace Parasite
{
	class AchievementsCollector
	{
		private:
			std::set<Achievement*> earnedAchievements;
		public:
			~AchievementsCollector();

			void addEarnedAchievement(Achievement *achievement);

			std::set<Achievement*> getEarnedAchievements();
	};
}
