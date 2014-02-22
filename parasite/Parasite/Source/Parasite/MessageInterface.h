#pragma once

#include <exception>
#include <string>

#include "Parasite/MessageInterfaceException.h"

namespace Parasite
{
	class MessageInterface
	{
		public:
			virtual ~MessageInterface()
			{
			}

			virtual void open() = 0;
			virtual void close() = 0;

			virtual void sendMessage(std::string message) = 0;
	};
}
