#include "Parasite/Watchers/KillScoreWatcher.h"

#include <set>

#include <BWAPI.h>

#include "Parasite/Achievement.h"
#include "Parasite/AchievementsCollector.h"

using std::set;
using namespace BWAPI;
using namespace Parasite;

namespace Parasite_Watchers
{
	KillScoreWatcher::KillScoreWatcher(Parasite::AchievementsCollector& achievementsCollector, BWAPI::Game& game, int scoreGapLimit, std::string achievementName)
	: game(game)
	, achievementName(achievementName)
	, scoreGapLimit(scoreGapLimit)
	, AchievementWatcher(achievementsCollector)
	{
	}

	void KillScoreWatcher::checkEndScore()
	{
		Player *self = game.self();
		int selfKillScore = self->getKillScore();

		set<Player*> players = game.getPlayers();
		set<Player*>::const_iterator it;

		int lowestScoreGap = selfKillScore;

		for (it = players.begin(); it != players.end(); it++)
		{
			Player *iteratedPlayer = *it;

			if (iteratedPlayer == self)
			{
				continue;
			}

			int scoreDifference = selfKillScore - iteratedPlayer->getKillScore();

			if (scoreDifference < lowestScoreGap)
			{
				lowestScoreGap = scoreDifference;
			}
		}

		if (lowestScoreGap >= scoreGapLimit)
		{
			Achievement *achievement = new Achievement(achievementName);
			achievementsCollector.addEarnedAchievement(achievement);
		}
	}
}
