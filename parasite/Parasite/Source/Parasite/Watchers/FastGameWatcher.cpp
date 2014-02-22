#include "Parasite/Watchers/FastGameWatcher.h"

#include <BWAPI.h>

#include "Parasite/AchievementsCollector.h"
#include "Parasite/Achievement.h"

using BWAPI::Game;
using Parasite::AchievementsCollector;
using Parasite::Achievement;

namespace Parasite_Watchers
{
	FastGameWatcher::FastGameWatcher(AchievementsCollector &achievementsCollector, Game &game, int secondsLimit, std::string achievementName)
	: game(game)
	, secondsLimit(secondsLimit)
	, achievementName(achievementName)
	, AchievementWatcher(achievementsCollector)
	{
	}

	void FastGameWatcher::checkEndTime()
	{
		if (game.elapsedTime() < secondsLimit)
		{
			Achievement *achievement = new Achievement(achievementName);
			achievementsCollector.addEarnedAchievement(achievement);
		}
	}
}
