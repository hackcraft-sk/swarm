#pragma once

#include <BWTA.h>

#include "BWU/Core/Drawable.h"

namespace BWU
{
	class BwtaDrawable : public Drawable
	{
		private:
			void drawBaseLocations(Canvas &canvas);
			void drawBaseLocation(BWTA::BaseLocation *baseLocation, Canvas &canvas);
			void drawRegions(Canvas &canvas);
			void drawRegion(BWTA::Region *region, Canvas &canvas);
			void drawChokepoints(Canvas &canvas);
		public:
			virtual void draw(Canvas &canvas);
	};
}
