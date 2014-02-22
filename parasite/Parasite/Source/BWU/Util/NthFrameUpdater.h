#pragma once

#include "BWU/Util/ConditionalUpdater.h"

#include <BWAPI.h>

namespace BWU
{
	class NthFrameUpdater : public ConditionalUpdater
	{
		private:
			BWAPI::Game *game;
			int nthFrame;
		protected:
			virtual bool checkCondition();
		public:
			NthFrameUpdater(Updateable *updateable, BWAPI::Game *game, int nthFrame);
	};
}
