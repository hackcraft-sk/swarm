#include "MessageOverlordConnection.h"

#include "Parasite/MessageInterface.h"

#include <sstream>

#include "Parasite/Achievement.h"

using std::set;
using std::string;
using std::stringstream;

namespace Parasite
{
	MessageOverlordConnection::MessageOverlordConnection(MessageInterface& messageInterface)
	: messageInterface(messageInterface)
	{
	}

	void MessageOverlordConnection::open()
	{
		messageInterface.open();
	}

	void MessageOverlordConnection::close()
	{
		messageInterface.close();
	}

	void MessageOverlordConnection::sendPlayerColor(PlayerColor playerColor)
	{
		string jsonMessage = "{\"playerColor\": \"" + playerColor.toHexString() +"\"}";
		messageInterface.sendMessage(jsonMessage);
	}

	void MessageOverlordConnection::sendAlivePing()
	{
		string jsonMessage = "{\"ping\": \"alive\"}";
		messageInterface.sendMessage(jsonMessage);
	}

	void MessageOverlordConnection::sendAchievements(set<Achievement*> achievements)
	{
		stringstream s;

		s << "{";
		s << "\"achievements\":";

			s << "[";
			bool first = true;
			set<Achievement*>::const_iterator it;
			for (it = achievements.begin(); it != achievements.end(); it++)
			{
				if (!first)
				{
					s << ",";
				}
				else
				{
					first = false;
				}

				Achievement *achievement = *it;

				string achievementName = achievement->getName();

				s << "{\"name\": \"" << achievementName << "\"}";
			}
			s << "]";

		s << "}";

		string jsonMessage = s.str();
		messageInterface.sendMessage(jsonMessage);
	}
}
