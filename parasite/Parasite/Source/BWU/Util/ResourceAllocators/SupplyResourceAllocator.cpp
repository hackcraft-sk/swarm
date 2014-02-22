#include "BWU/Util/ResourceAllocators/SupplyResourceAllocator.h"

#include "BWU/Core/ResourcePool.h"

using BWAPI::Player;
using BWAPI::Race;

namespace BWU
{
	SupplyResourceAllocator::SupplyResourceAllocator(Player* player, ResourcePool& supplyPool)
	: player(player)
	, race(player->getRace())
	, supplyPool(supplyPool)
	, lastTotalSupply(0)
	, lastUsedSupply(0)
	{
		setInitialSupplies();
	}

	SupplyResourceAllocator::SupplyResourceAllocator(Player *player, Race race, ResourcePool &supplyPool)
	: player(player)
	, race(race)
	, supplyPool(supplyPool)
	, lastTotalSupply(0)
	, lastUsedSupply(0)
	{
		setInitialSupplies();
	}

	void SupplyResourceAllocator::setInitialSupplies()
	{
		int initialFreeSupply = player->supplyTotal(race) - player->supplyUsed(race);
		supplyPool.modifyQuantityBy(initialFreeSupply);

		lastTotalSupply = player->supplyTotal();
		lastUsedSupply = player->supplyUsed();
	}

	void SupplyResourceAllocator::update()
	{
		unsigned int totalSupply = player->supplyTotal(race);
		unsigned int usedSupply = player->supplyUsed(race);

		int delta = 0;

		int totalSupplyDelta = totalSupply - lastTotalSupply;
		if (totalSupplyDelta != 0)
		{
			// our supply provider was created / destroyed
			delta += totalSupplyDelta;
		}

		int usedSupplyDelta = lastUsedSupply - usedSupply;
		if (usedSupplyDelta > 0)
		{
			// some of our units died
			delta += usedSupplyDelta;
		}

		if (delta != 0)
		{
			supplyPool.modifyQuantityBy(delta);
		}

		lastTotalSupply = totalSupply;
		lastUsedSupply = usedSupply;
	}
}
