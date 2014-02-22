#pragma once

#include "BWU/Util/ResourceAllocators/MinedResourceAllocator.h"

namespace BWAPI
{
	class Player;
}

namespace BWU
{
	class ResourcePool;

	class MineralsResourceAllocator : public MinedResourceAllocator
	{
		protected:
			virtual unsigned int getGatheredResourcesCount();
		public:
			MineralsResourceAllocator(BWAPI::Player *player, ResourcePool &mineralsPool);
	};
}
