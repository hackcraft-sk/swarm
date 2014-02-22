#include "BWU/Util/ResourceAllocators/MinedResourceAllocator.h"

#include "BWU/Core/ResourcePool.h"

using BWAPI::Player;

namespace BWU
{
	MinedResourceAllocator::MinedResourceAllocator(Player* player, ResourcePool &resourcePool)
	: player(player)
	, resourcePool(resourcePool)
	, lastGatheredCount(0)
	{
	}

	Player *MinedResourceAllocator::getPlayer()
	{
		return player;
	}

	void MinedResourceAllocator::update()
	{
		unsigned int gatheredResourcesCount = getGatheredResourcesCount();
		int delta = gatheredResourcesCount - lastGatheredCount;

		if (delta != 0)
		{
			resourcePool.modifyQuantityBy(delta);
		}

		lastGatheredCount = gatheredResourcesCount;
	}
}
