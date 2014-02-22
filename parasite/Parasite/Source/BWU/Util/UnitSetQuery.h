#pragma once

#include <set>

namespace BWAPI
{
	class Unit;
}

namespace BWU
{
	class UnitSetQuery
	{
		private:
			std::set<BWAPI::Unit*> units;
		public:
			UnitSetQuery(std::set<BWAPI::Unit*> units);

			template <class S>
			UnitSetQuery select(S selector);

			template <class S>
			UnitSetQuery select(S selector, bool isTrue);

			std::set<BWAPI::Unit*> toSet();
			BWAPI::Unit *toFirstUnit();
	};

	template <class S>
	UnitSetQuery UnitSetQuery::select(S selector)
	{
		return select(selector, true);
	}

	template <class S>
	UnitSetQuery UnitSetQuery::select(S selector, bool isTrue)
	{
		set<BWAPI::Unit*> acceptableUnits;
		set<BWAPI::Unit*>::iterator it;
		for (it = units.begin(); it != units.end(); it++)
		{
			BWAPI::Unit &unit = **it;

			if (selector(unit) == isTrue)
			{
				acceptableUnits.insert(&unit);
			}
		}

		return UnitSetQuery(acceptableUnits);
	}
}
