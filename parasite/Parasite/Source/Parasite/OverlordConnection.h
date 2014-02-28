#pragma once

#include <set>
#include <map>
#include <string>

#include "PlayerColor.h"

namespace Parasite
{
	class Achievement;

	class OverlordConnection
	{
		public:
			virtual ~OverlordConnection()
			{
			}

			virtual void open() = 0;
			virtual void close() = 0;

			virtual void sendPlayerColor(PlayerColor playerColor) = 0;
			virtual void sendAlivePing() = 0;
			virtual void sendScores(std::map<std::string, int> scores) = 0;
			virtual void sendAchievements(std::set<Achievement*> achievements) = 0;
	};
}
