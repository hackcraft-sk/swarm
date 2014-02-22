#pragma once

#include <map>

#include <BWAPI.h>

#include "BWU/Core/Point.h"
#include "BWU/Map/LayerDrawable.h"

namespace BWU
{
	class TileLayer;
	class Canvas;

	class ColorLayerDrawable : public LayerDrawable
	{
		private:
			std::map<int, BWAPI::Color> &colors;
		protected:
			virtual void drawCell(Canvas &canvas, Point position, int value);
		public:
			ColorLayerDrawable(Layer &layer, std::map<int, BWAPI::Color> &colors);
	};
}
