#pragma once

#include "Parasite/MessageInterface.h"

namespace BWU
{
	class Log;
}

namespace Parasite
{
	class LogMessageInterface : public MessageInterface
	{
		private:
			BWU::Log &log;
		public:
			LogMessageInterface(BWU::Log &log);

			virtual void open();
			virtual void close();

			virtual void sendMessage(std::string message);
	};
}
