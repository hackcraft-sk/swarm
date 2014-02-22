#pragma once

namespace Parasite
{
	class MessageInterfaceException: public std::exception
	{
		private:
			std::string message;
		public:
			MessageInterfaceException(std::string message)
					: message(message)
			{
			}

			const char* what() const
			{
				return message.c_str();
			}
	};
}
