#include "LogMessageInterface.h"

#include "BWU/Core/Log.h"

using std::string;
using BWU::Log;

namespace Parasite
{
	Parasite::LogMessageInterface::LogMessageInterface(Log& log)
	: log(log)
	{
	}

	void LogMessageInterface::open()
	{
	}

	void LogMessageInterface::close()
	{
	}

	void Parasite::LogMessageInterface::sendMessage(string message)
	{
		log.print("Message: " + message);
	}
}
