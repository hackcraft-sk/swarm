#pragma once

#include "BWU/Util/UnitSetQuery.h"

#include <set>
#include <functional>

namespace BWAPI
{
	class Unit;
}

namespace BWU
{
	namespace UnitSetQuerySelectors
	{
		class IsBuilding
		{
			public:
				bool operator() (BWAPI::Unit &unit)
				{
					return unit.getType().isBuilding();
				}
		};

		class IsWorker
		{
			public:
				bool operator() (BWAPI::Unit &unit)
				{
					return unit.getType().isWorker();
				}
		};

		class IsResourceDepot
		{
			public:
				bool operator() (BWAPI::Unit &unit)
				{
					return unit.getType().isResourceDepot();
				}
		};

		class IsMineralField
		{
			public:
				bool operator() (BWAPI::Unit &unit)
				{
					return unit.getType().isMineralField();
				}
		};

		class IsVespeneGeyser
		{
			public:
				bool operator() (BWAPI::Unit &unit)
				{
					return unit.getType() == BWAPI::UnitTypes::Resource_Vespene_Geyser;
				}
		};

		class IsVisible
		{
			public:
				bool operator() (BWAPI::Unit &unit)
				{
					return unit.isVisible();
				}
		};

		class IsCompleted
		{
			public:
				bool operator() (BWAPI::Unit &unit)
				{
					return unit.isCompleted();
				}
		};

		class InSet
		{
			private:
				std::set<BWAPI::Unit*> &units;
			public:
				InSet(std::set<BWAPI::Unit*> &units)
				: units(units)
				{
				}

				bool operator() (BWAPI::Unit &unit)
				{
					return units.find(&unit) != units.end();
				}
		};

		class IsType
		{
			private:
				BWAPI::UnitType type;
			public:
				IsType(BWAPI::UnitType type)
				: type(type)
				{
				}

				bool operator() (BWAPI::Unit &unit)
				{
					return unit.getType() == type;
				}
		};

		template <class O>
		class HitPoints
		{
			private:
				int value;
			public:
				HitPoints(int value)
				: value(value)
				{
				}

				bool operator() (BWAPI::Unit &unit)
				{
					return O()(unit.getHitPoints(), value);
				}
		};
	}
}
