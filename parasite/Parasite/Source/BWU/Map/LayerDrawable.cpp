#include "BWU/Map/LayerDrawable.h"

#include "BWU/Map/Layer.h"

namespace BWU
{
	LayerDrawable::LayerDrawable(Layer &layer)
	: layer(layer)
	{
	}

	void LayerDrawable::draw(Canvas &canvas)
	{
		Dimension dimension = layer.getSize();
		int width = dimension.getWidth();
		int height = dimension.getHeight();

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int value = layer.getValue(Point(x, y));

				drawCell(canvas, Point(x, y), value);
			}
		}
	}
}
