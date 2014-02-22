#include "GameTime.h"

using namespace BWAPI;

namespace BWU
{
	GameTime::GameTime(BWAPI::Game &game)
	: game(game)
	{
	}

	unsigned int GameTime::getTime()
	{
		return game.getFrameCount();
	}
}
