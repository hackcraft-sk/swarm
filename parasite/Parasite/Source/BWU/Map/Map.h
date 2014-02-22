#pragma once

#include "BWU/Core/Dimension.h"
#include "BWU/Map/ReadonlyLayer.h"

namespace BWU
{
	class Map
	{
		public:
			virtual ~Map() {};

			virtual Dimension getPixelSize() const = 0;
			virtual Dimension getTileSize() const = 0;

			virtual std::string getName() const = 0;
			virtual std::string getHash() const = 0;

			virtual std::string getFileName() const = 0;
			virtual std::string getPathName() const = 0;

			virtual void ping(BWAPI::Position &position) const = 0;
			virtual void ping(int x, int y) const = 0;

			virtual ReadonlyLayer &getBuildableLayer() = 0;
	};
}
