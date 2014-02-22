#include "Parasite/Watchers/GameResultWatcher.h"

#include <string>

#include "Parasite/AchievementsCollector.h"

using std::string;
using Parasite::AchievementsCollector;
using Parasite::Achievement;

namespace Parasite_Watchers
{
	GameResultWatcher::GameResultWatcher(AchievementsCollector &achievementsCollector)
	: AchievementWatcher(achievementsCollector)
	{
	}

	void GameResultWatcher::setBotGameResult(bool wins)
	{
		string name = (wins) ? "victory" : "defeat";

		Achievement *achievement = new Achievement(name);

		achievementsCollector.addEarnedAchievement(achievement);
	}
}
