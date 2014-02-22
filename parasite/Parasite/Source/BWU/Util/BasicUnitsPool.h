#pragma once

#include <map>

#include "BWU/Core/UnitsPool.h"
#include "BWU/Core/UnitsOwner.h"

namespace BWAPI
{
	class Unit;
	class Player;
}

namespace BWU
{
	class BasicUnitsPool : public UnitsPool
	{
		private:
			class UsageRecord;
			typedef std::map<BWAPI::Unit*, UsageRecord*> UsageRecordsMap;

			std::set<BWAPI::Unit*> managedUnits;

			UsageRecordsMap usageRecords;

			std::set<BWAPI::Unit*> getUnitsAcquierableWithPriority(BWU::Priority::Enum priority);

			bool requestUnit(BWAPI::Unit &unit, UnitsOwner &unitsOwner, BWU::Priority::Enum priority, UnitsOwner::UnitOwnershipListener &listener);
			void releaseUnit(BWAPI::Unit &unit, UnitsOwner &unitsOwner);

			friend class UnitsPoolDrawable;
		public:
			virtual void addUnit(BWAPI::Unit &unit);
			virtual void removeUnit(BWAPI::Unit &unit);

			virtual UnitsOwner *createUnitsOwner(std::string name);

		private:
			class UsageRecord
			{
				private:
					UnitsOwner &unitsOwner;
					BWU::Priority::Enum priority;
					UnitsOwner::UnitOwnershipListener &listener;
				public:
					UsageRecord(UnitsOwner &unitsOwner, BWU::Priority::Enum priority, UnitsOwner::UnitOwnershipListener &listener);

					UnitsOwner &getUnitsOwner();
					BWU::Priority::Enum getPriority();
					UnitsOwner::UnitOwnershipListener &getUnitsOwnerListener();
			};

			class BasicUnitsOwner : public UnitsOwner
			{
				private:
					BasicUnitsPool &manager;
					std::string name;

					std::set<BWAPI::Unit*> ownedUnits;
				public:
					BasicUnitsOwner(BasicUnitsPool &manager, std::string name);

					virtual std::string getName();

					virtual std::set<BWAPI::Unit*> getUnitsAcquierableWithPriority(BWU::Priority::Enum priority);

					virtual bool acquireUnit(BWAPI::Unit &unit, BWU::Priority::Enum priority, UnitsOwner::UnitOwnershipListener &listener);
					virtual void releaseUnit(BWAPI::Unit &unit);
			};
	};
}
