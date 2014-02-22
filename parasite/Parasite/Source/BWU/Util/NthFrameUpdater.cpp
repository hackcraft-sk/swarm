#include "BWU/Util/NthFrameUpdater.h"

using namespace BWAPI;

namespace BWU
{
	NthFrameUpdater::NthFrameUpdater(Updateable *updateable, BWAPI::Game *game, int nthFrame)
	: ConditionalUpdater(updateable)
	, game(game)
	, nthFrame(nthFrame)
	{
	}

	bool NthFrameUpdater::checkCondition()
	{
		int actualFrame = game->getFrameCount();

		return actualFrame % nthFrame == 0;
	}
}
