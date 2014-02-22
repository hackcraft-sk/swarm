#include "Parasite/MockOverlordConnection.h"

#include <string>

#include "BWU/Core/Log.h"
#include "Parasite/Achievement.h"

using std::set;
using BWU::Log;

namespace Parasite
{
	MockOverlordConnection::MockOverlordConnection(Log &log)
	: log(log)
	{
	}

	void MockOverlordConnection::open()
	{
		log.print("Connected");
	}

	void MockOverlordConnection::close()
	{
		log.print("Connection closed");
	}

	void MockOverlordConnection::sendAlivePing()
	{
		log.print(".");
	}

	void MockOverlordConnection::sendPlayerColor(PlayerColor playerColor)
	{
		log.print("Player color: " + playerColor.toHexString());
	}

	void MockOverlordConnection::sendAchievements(set<Achievement*> achievements)
	{
		log.print("Sending achievements: ");

		set<Achievement*>::const_iterator it;
		for (it = achievements.begin(); it != achievements.end(); it++)
		{
			Achievement *achievement = *it;

			log.print(achievement->getName());
		}
	}
}
