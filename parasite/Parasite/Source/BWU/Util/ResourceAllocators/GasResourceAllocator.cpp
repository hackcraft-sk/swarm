#include "BWU/Util/ResourceAllocators/GasResourceAllocator.h"

#include <BWAPI.h>
#include "BWU/Core/ResourcePool.h"

using BWAPI::Player;

namespace BWU
{
	GasResourceAllocator::GasResourceAllocator(Player* player, ResourcePool &mineralsPool)
	: MinedResourceAllocator(player, mineralsPool)
	{
	}

	unsigned int GasResourceAllocator::getGatheredResourcesCount()
	{
		Player *player = getPlayer();
		return player->gatheredGas();
	}
}
