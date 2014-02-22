#include "Parasite/SocketMessageInterface.h"

#include "Parasite/MessageInterfaceException.h"

#include <string>
#include <sstream>

#include <cstdio>

#include <BWAPI.h>

#include "BWU/Core/Log.h"

using std::string;
using std::stringstream;
using BWU::Log;

#pragma comment (lib, "Ws2_32.lib")
#pragma comment (lib, "Mswsock.lib")
#pragma comment (lib, "AdvApi32.lib")

namespace Parasite
{
	SocketMessageInterface::SocketMessageInterface(string address, Log &log)
			: address(address), log(log), defaultPort("11998"), ConnectSocket(INVALID_SOCKET)
	{
	}

	void SocketMessageInterface::open()
	{
		WSADATA wsaData;
		struct addrinfo *result = NULL, *ptr = NULL, hints;
		int iResult;

		// Initialize Winsock
		iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
		if (iResult != 0)
		{
			throw new MessageInterfaceException("Function WSAStartup failed: " + iResult);
		}

		ZeroMemory( &hints, sizeof(hints) );
		hints.ai_family = AF_UNSPEC;
		hints.ai_socktype = SOCK_STREAM;
		hints.ai_protocol = IPPROTO_TCP;
		hints.ai_addr;

		// Resolve the server address and port
		iResult = getaddrinfo(address.c_str(), defaultPort, &hints, &result);
		if (iResult != 0)
		{
			WSACleanup();
			throw new MessageInterfaceException("Function getaddrinfo failed: " + iResult);
		}

		// Attempt to connect to an address until one succeeds
		for (ptr = result; ptr != NULL; ptr = ptr->ai_next)
		{

			// Create a SOCKET for connecting to server
			ConnectSocket = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);
			if (ConnectSocket == INVALID_SOCKET)
			{
				WSACleanup();
				throw new MessageInterfaceException("Opening socket failed: " + WSAGetLastError());
			}

			// Connect to server
			iResult = connect(ConnectSocket, ptr->ai_addr, (int) ptr->ai_addrlen);
			if (iResult == SOCKET_ERROR)
			{
				closesocket(ConnectSocket);
				ConnectSocket = INVALID_SOCKET;
				continue;
			}
			break;
		}

		freeaddrinfo(result);

		if (ConnectSocket == INVALID_SOCKET)
		{
			WSACleanup();
			throw new MessageInterfaceException("ConnectSocket == INVALID_SOCKET");
		}
	}

	void SocketMessageInterface::sendMessage(string message)
	{
		int iResult = send(ConnectSocket, message.c_str(), message.size(), 0);
		if (iResult == SOCKET_ERROR)
		{
			log.printf("send failed with error: %d", WSAGetLastError());
			closesocket(ConnectSocket);
			WSACleanup();

			throw new MessageInterfaceException("Can't send message.");
		}
	}

	void SocketMessageInterface::close()
	{
		closesocket(ConnectSocket);
		WSACleanup();
	}
}
