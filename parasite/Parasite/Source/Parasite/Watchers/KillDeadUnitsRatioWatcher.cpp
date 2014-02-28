#include "Parasite/Watchers/KillDeadUnitsRatioWatcher.h"

#include <string>
#include <set>

#include <BWAPI.h>

#include "Parasite/AchievementsCollector.h"
#include "Parasite/Achievement.h"

using std::string;
using std::set;
using namespace BWAPI;
using namespace Parasite;

namespace Parasite_Watchers
{
	KillDeadUnitsRatioWatcher::KillDeadUnitsRatioWatcher(Parasite::AchievementsCollector& achievementsCollector, Player &player, int killRatio, int deadRatio, string achievementName)
			: player(player), killRatio(killRatio), deadRatio(deadRatio), achievementName(achievementName), AchievementWatcher(achievementsCollector)
	{
	}

	void KillDeadUnitsRatioWatcher::checkKillDeadUnitsRatio()
	{
		int totalUnitsKilled = 0;
		int totalUnitsLost = 0;

		set<UnitType> unitTypes = UnitTypes::allUnitTypes();
		set<UnitType>::const_iterator it;
		for (it = unitTypes.begin(); it != unitTypes.end(); it++)
		{
			UnitType unitType = *it;

			totalUnitsKilled += player.killedUnitCount(unitType);
			totalUnitsLost += player.deadUnitCount(unitType);
		}

		if ((double)totalUnitsKilled / killRatio > (double)totalUnitsLost / deadRatio)
		{
			Achievement *achievement = new Achievement(achievementName);
			achievementsCollector.addEarnedAchievement(achievement);
		}
	}
}
