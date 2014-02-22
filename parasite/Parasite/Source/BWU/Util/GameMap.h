#pragma once

#include <BWAPI.h>

#include <set>

#include "BWU/Map/Map.h"
#include "BWU/Map/BroodWarMapLayer.h"

namespace BWU
{
	class GameMap : public Map
	{
		private:
			static Dimension createSize(BWAPI::Game &game);
			static Dimension createTileSize(BWAPI::Game &game);

			BWAPI::Game &game;

			Dimension size;
			Dimension tileSize;

			BroodWarMapLayer<BroodWarMapSelectors::BuildableSelector> buildableLayer;
		public:
			GameMap(BWAPI::Game &game);

			virtual Dimension getPixelSize() const;
			virtual Dimension getTileSize() const;

			virtual std::string getName() const;
			virtual std::string getHash() const;

			virtual std::string getFileName() const;
			virtual std::string getPathName() const;

			virtual void ping(BWAPI::Position &position) const;
			virtual void ping(int x, int y) const;

			virtual ReadonlyLayer &getBuildableLayer();
	};
}
