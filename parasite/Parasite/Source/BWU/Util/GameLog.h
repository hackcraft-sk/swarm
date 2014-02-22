#pragma once

#include <string>

#include <BWAPI.h>

#include "BWU/Core/Log.h"

namespace BWU
{
	class GameLog : public Log
	{
		private:
			BWAPI::Game &game;
		public:
			GameLog(BWAPI::Game &game);
			virtual void printf(const char *format, ...);
			virtual void print(std::string text);
	};
}
