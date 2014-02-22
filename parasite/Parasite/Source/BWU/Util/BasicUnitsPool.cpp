#include "BWU/Util/BasicUnitsPool.h"

#include <BWAPI.h>

using std::set;
using std::make_pair;
using std::string;

using BWAPI::Player;
using BWAPI::Unit;

using BWU::Priority;

namespace BWU
{

	void BasicUnitsPool::addUnit(BWAPI::Unit& unit)
	{
		managedUnits.insert(&unit);
	}

	void BasicUnitsPool::removeUnit(BWAPI::Unit &unit)
	{
		managedUnits.erase(&unit);
	}

	UnitsOwner *BasicUnitsPool::createUnitsOwner(std::string name)
	{
		return new BasicUnitsOwner(*this, name);
	}

	set<Unit*> BasicUnitsPool::getUnitsAcquierableWithPriority(Priority::Enum priority)
	{
		set<Unit*> acquierableUnits;

		set<Unit*>::const_iterator it;
		for (it = managedUnits.begin(); it != managedUnits.end(); it++)
		{
			Unit *unit = *it;

			UsageRecordsMap::iterator urmit = usageRecords.find(unit);

			if (urmit == usageRecords.end())
			{
				acquierableUnits.insert(unit);
			}
			else
			{
				UsageRecord *usageRecord = urmit->second;
				if (usageRecord->getPriority() < priority)
				{
					acquierableUnits.insert(unit);
				}
			}
		}
		return acquierableUnits;
	}

	bool BasicUnitsPool::requestUnit(Unit &unit, UnitsOwner &unitsOwner, Priority::Enum priority, UnitsOwner::UnitOwnershipListener &listener)
	{
		UsageRecordsMap::iterator urmit = usageRecords.find(&unit);

		if (urmit == usageRecords.end())
		{
			// no usage records, so unit is free
			UsageRecord *newUsageRecord = new UsageRecord(unitsOwner, priority, listener);
			usageRecords.insert(make_pair(&unit, newUsageRecord));

			return true;
		}
		else
		{
			UsageRecord *usageRecord = urmit->second;
			if (usageRecord->getPriority() < priority)
			{
				// unit has usage record, but with lower priority, so we can acquire it
				usageRecord->getUnitsOwnerListener().onUnitRemoved(unit);

				usageRecords.erase(urmit);
				delete usageRecord;

				UsageRecord *newUsageRecord = new UsageRecord(unitsOwner, priority, listener);
				usageRecords.insert(make_pair(&unit, newUsageRecord));

				return true;
			}
			else
			{
				// unit is owned with higher priority
				return false;
			}
		}
	}

	void BasicUnitsPool::releaseUnit(Unit &unit, UnitsOwner &unitsOwner)
	{
		UsageRecordsMap::iterator urmit = usageRecords.find(&unit);

		if (urmit != usageRecords.end())
		{
			UsageRecord *usageRecord = urmit->second;

			if (&usageRecord->getUnitsOwner() != &unitsOwner)
			{
				// notify error;
			}
			else
			{
				usageRecords.erase(urmit);
				delete usageRecord;
			}
		}
		else
		{
			// notify error
		}
	}

	BasicUnitsPool::UsageRecord::UsageRecord(UnitsOwner &unitsOwner, BWU::Priority::Enum priority, UnitsOwner::UnitOwnershipListener &listener)
	: unitsOwner(unitsOwner)
	, priority(priority)
	, listener(listener)
	{
	}

	UnitsOwner &BasicUnitsPool::UsageRecord::getUnitsOwner()
	{
		return unitsOwner;
	}

	BWU::Priority::Enum BasicUnitsPool::UsageRecord::getPriority()
	{
		return priority;
	}

	UnitsOwner::UnitOwnershipListener& BasicUnitsPool::UsageRecord::getUnitsOwnerListener()
	{
		return listener;
	}

	BasicUnitsPool::BasicUnitsOwner::BasicUnitsOwner(BasicUnitsPool& manager, string name)
	: manager(manager)
	, name(name)
	{
	}

	string BasicUnitsPool::BasicUnitsOwner::getName()
	{
		return name;
	}

	set<Unit*> BasicUnitsPool::BasicUnitsOwner::getUnitsAcquierableWithPriority(BWU::Priority::Enum priority)
	{
		return manager.getUnitsAcquierableWithPriority(priority);
	}

	bool BasicUnitsPool::BasicUnitsOwner::acquireUnit(BWAPI::Unit &unit, BWU::Priority::Enum priority, UnitsOwner::UnitOwnershipListener& listener)
	{
		bool result = manager.requestUnit(unit, *this, priority, listener);

		if (result)
		{
			ownedUnits.insert(&unit);
		}

		return result;
	}

	void BasicUnitsPool::BasicUnitsOwner::releaseUnit(BWAPI::Unit &unit)
	{
		if (ownedUnits.find(&unit) == ownedUnits.end())
		{
			// TODO
			// notify error
		}
		else
		{
			manager.releaseUnit(unit, *this);
		}
	}
}
