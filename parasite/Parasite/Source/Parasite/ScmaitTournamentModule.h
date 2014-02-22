#pragma once

#include <BWAPI.h>

namespace Parasite
{
	class ScmaitTournamentModule: public BWAPI::TournamentModule
	{
		private:
			BWAPI::Game *game;
		public:
			ScmaitTournamentModule(BWAPI::Game *game);

			virtual bool onAction(int actionType);
			virtual bool onAction(int actionType, void *parameter);

			virtual void onFirstAdvertisement();
	};
}
