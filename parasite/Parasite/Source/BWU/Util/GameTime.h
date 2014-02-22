#pragma once

#include "BWU/Core/EnvironmentTime.h"

#include <BWAPI.h>

namespace BWU
{
	class GameTime : public BWU::EnvironmentTime
	{
		private:
			BWAPI::Game &game;
		public:
			GameTime(BWAPI::Game &game);

			virtual unsigned int getTime();
	};
}
