#pragma once

namespace Parasite
{
	class AchievementsCollector;
}

namespace Parasite_Watchers
{
	class AchievementWatcher
	{
		protected:
			Parasite::AchievementsCollector &achievementsCollector;
		public:
			AchievementWatcher(Parasite::AchievementsCollector &achievementsCollector);

			virtual ~AchievementWatcher();
	};
}
