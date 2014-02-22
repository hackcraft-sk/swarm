#pragma once

#include <BWAPI.h>

#include "BWU/Core/Updateable.h"

namespace BWU
{
	class ResourcePool;

	class MinedResourceAllocator : public Updateable
	{
		private:
			BWAPI::Player *player;
			ResourcePool &resourcePool;

			unsigned int lastGatheredCount;
		protected:
			BWAPI::Player *getPlayer();

			virtual unsigned int getGatheredResourcesCount() = 0;
		public:
			MinedResourceAllocator(BWAPI::Player *player, ResourcePool &resourcePool);

			virtual void update();
	};
}
