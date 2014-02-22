#include "BWU/Util/GameLog.h"

#include <cstdio>
#include <cstdarg>

namespace BWU
{
	GameLog::GameLog(BWAPI::Game &game)
	: game(game)
	{
	}

	void GameLog::printf(const char *format, ...)
	{
		int len;
		char *buffer;

		std::va_list arguments;

		va_start(arguments, format);
			len = _vscprintf(format, arguments) + 1;
			buffer = new char[len * sizeof(char)];
			
			vsprintf_s(buffer, len, format, arguments);
		va_end(arguments);

		game.printf("%s", buffer);

		delete[] buffer;
	}

	void GameLog::print(std::string text)
	{
		printf("%s", text.c_str());
	}
}
