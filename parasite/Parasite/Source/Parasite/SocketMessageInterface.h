#pragma once

#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>

#include <cstdlib>
#include <cstdio>

#include <string>

#include "Parasite/MessageInterface.h"

namespace BWU
{
	class Log;
}

namespace Parasite
{
	class SocketMessageInterface : public MessageInterface
	{
		private:
			SOCKET ConnectSocket;

			std::string address;
			const char *defaultPort;

			void buildString(char* output, const char* key, const char* value);

		public:
			SocketMessageInterface(std::string address);

			virtual void open();
			virtual void close();

			virtual void sendMessage(std::string message);
	};
}
