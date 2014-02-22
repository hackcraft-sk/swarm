#include "Parasite/Watchers/AchievementWatcher.h"

#include "Parasite/AchievementsCollector.h"

using Parasite::AchievementsCollector;

namespace Parasite_Watchers
{
	AchievementWatcher::AchievementWatcher(AchievementsCollector &achievementsCollector)
	: achievementsCollector(achievementsCollector)
	{
	}

	AchievementWatcher::~AchievementWatcher()
	{
	}
}

