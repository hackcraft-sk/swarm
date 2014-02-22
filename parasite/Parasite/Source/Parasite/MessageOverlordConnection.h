#pragma once

#include "Parasite/OverlordConnection.h"

namespace Parasite
{
	class MessageInterface;

	class MessageOverlordConnection : public OverlordConnection
	{
		private:
			MessageInterface &messageInterface;
		public:
			MessageOverlordConnection(MessageInterface &messageInterface);

			virtual void open();
			virtual void close();

			virtual void sendPlayerColor(PlayerColor playerColor);
			virtual void sendAlivePing();
			virtual void sendAchievements(std::set<Achievement*> achievements);
	};
}
