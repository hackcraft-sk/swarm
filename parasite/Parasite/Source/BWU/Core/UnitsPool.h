#pragma once

#include <set>
#include <string>

#include "BWU/Core/Priority.h"

namespace BWAPI
{
	class Unit;
}

namespace BWU
{
	class UnitSetFilter;
	class UnitsOwner;

	class UnitsPool
	{
		public:
			class UnitsContract;
			class ContractListener;

			virtual ~UnitsPool() {}

			virtual void addUnit(BWAPI::Unit &unit) = 0;
			virtual void removeUnit(BWAPI::Unit &unit) = 0;

			virtual UnitsOwner *createUnitsOwner(std::string name) = 0;
	};
}
