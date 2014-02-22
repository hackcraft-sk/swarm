#include "Parasite/AchievementsCollector.h"

using std::set;

namespace Parasite
{
	AchievementsCollector::~AchievementsCollector()
	{
		set<Achievement*>::const_iterator it;
		for (it = earnedAchievements.begin(); it != earnedAchievements.end(); it++)
		{
			Achievement *achievement = *it;

			delete achievement;
		}

		earnedAchievements.clear();
	}

	void AchievementsCollector::addEarnedAchievement(Achievement *achievement)
	{
		earnedAchievements.insert(achievement);
	}

	set<Achievement*> AchievementsCollector::getEarnedAchievements()
	{
		return earnedAchievements;
	}
}
