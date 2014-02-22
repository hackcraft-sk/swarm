#pragma once

namespace BWU
{
	class UnitsOwner
	{
		public:
			class UnitOwnershipListener;

			virtual ~UnitsOwner() {}

			virtual std::string getName() = 0;

			virtual std::set<BWAPI::Unit*> getUnitsAcquierableWithPriority(BWU::Priority::Enum priority) = 0;

			virtual bool acquireUnit(BWAPI::Unit &unit, BWU::Priority::Enum priority, UnitOwnershipListener &listener) = 0;
			virtual void releaseUnit(BWAPI::Unit &unit) = 0;

			class UnitOwnershipListener
			{
				public:
					virtual ~UnitOwnershipListener() {}

					virtual void onUnitRemoved(BWAPI::Unit &unit) = 0;
			};
	};
}
