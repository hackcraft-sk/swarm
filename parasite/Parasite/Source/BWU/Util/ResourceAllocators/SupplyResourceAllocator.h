#pragma once

#include <BWAPI.h>

#include "BWU/Core/Updateable.h"

namespace BWAPI
{
	class Player;
}

namespace BWU
{
	class ResourcePool;

	class SupplyResourceAllocator : public Updateable
	{
		private:
			BWAPI::Player *player;
			BWAPI::Race race;
			ResourcePool &supplyPool;

			unsigned int lastTotalSupply;
			unsigned int lastUsedSupply;

			void setInitialSupplies();
		public:
			SupplyResourceAllocator(BWAPI::Player *player, ResourcePool &supplyPool);
			SupplyResourceAllocator(BWAPI::Player *player, BWAPI::Race race, ResourcePool &supplyPool);

			virtual void update();
	};
}
