#include "BWU/Util/UnitSetQuery.h"

namespace BWU
{
	UnitSetQuery::UnitSetQuery(std::set<BWAPI::Unit*> units)
	: units(units)
	{
	}

	std::set<BWAPI::Unit*> UnitSetQuery::toSet()
	{
		return units;
	}

	BWAPI::Unit *UnitSetQuery::toFirstUnit()
	{
		if (units.empty())
		{
			return NULL;
		}
		else
		{
			return *units.begin();
		}
	}
}
