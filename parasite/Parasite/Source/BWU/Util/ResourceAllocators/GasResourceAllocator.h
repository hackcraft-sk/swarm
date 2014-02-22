#pragma once

#include "BWU/Util/ResourceAllocators/MinedResourceAllocator.h"

namespace BWAPI
{
	class Player;
}

namespace BWU
{
	class ResourcePool;

	class GasResourceAllocator : public MinedResourceAllocator
	{
		protected:
			virtual unsigned int getGatheredResourcesCount();
		public:
			GasResourceAllocator(BWAPI::Player *player, ResourcePool &gasPool);
	};
}
