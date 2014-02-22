#pragma once

#include "Parasite/OverlordConnection.h"

namespace BWU
{
	class Log;
}

namespace Parasite
{
	class MockOverlordConnection: public OverlordConnection
	{
		private:
			BWU::Log &log;
		public:
			MockOverlordConnection(BWU::Log &log);

			virtual void open();
			virtual void close();

			virtual void sendPlayerColor(PlayerColor playerColor);
			virtual void sendAlivePing();
			virtual void sendAchievements(std::set<Achievement*> achievements);
	};
}
