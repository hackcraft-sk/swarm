#include "BWU/Util/ResourceAllocators/MineralsResourceAllocator.h"

#include <BWAPI.h>

#include "BWU/Core/ResourcePool.h"

using BWAPI::Player;

namespace BWU
{
	MineralsResourceAllocator::MineralsResourceAllocator(Player* player, ResourcePool &mineralsPool)
	: MinedResourceAllocator(player, mineralsPool)
	{
	}

	unsigned int MineralsResourceAllocator::getGatheredResourcesCount()
	{
		Player *player = getPlayer();
		return player->gatheredMinerals();
	}
}
