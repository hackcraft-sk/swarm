#pragma once

#include <BWAPI.h>

namespace BWU
{
	class BuildingPlacer
	{
		public:
			~BuildingPlacer() {};

			BWAPI::TilePosition getPositionFor(BWAPI::UnitType buildingType);
			BWAPI::TilePosition getPositionFor(BWAPI::UnitType buildingType, BWAPI::TilePosition origin);
	};
}
