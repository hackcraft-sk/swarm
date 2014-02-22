#include "BWU/Map/ColorLayerDrawable.h"

#include "BWU/Core/Canvas.h"

#include <BWAPI.h>

using std::map;
using namespace BWAPI;

namespace BWU
{
	ColorLayerDrawable::ColorLayerDrawable(Layer &layer, map<int, Color> &colors)
	: LayerDrawable(layer)
	, colors(colors)
	{
	}

	void ColorLayerDrawable::drawCell(Canvas &canvas, Point position, int value)
	{
		int x = position.getX() * TILE_SIZE;
		int y = position.getY() * TILE_SIZE;

		map<int, Color>::iterator it = colors.find(value);
		if (it == colors.end())
		{
			return;
		}

		Color color = it->second;

		int padding = 2;
		x += padding;
		y += padding;
		int width = TILE_SIZE - padding * 2;
		int height = TILE_SIZE - padding * 2;;

		canvas
		.setPosition(x, y)
		.setColor(color)
		.setSolid(false)
		.drawBox(width, height);
	}
}
