#pragma once

namespace BWU
{
	class Log
	{
		public:
			virtual ~Log() {};
			virtual void printf(const char *format, ...) = 0;
			virtual void print(std::string text) = 0;
	};
}
