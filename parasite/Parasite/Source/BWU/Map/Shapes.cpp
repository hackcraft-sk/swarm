#include "BWU/Map/Shapes.h"

using std::list;

namespace BWU
{
	RectangleLayerShape::RectangleLayerShape(int width, int height, int value)
	: width(width)
	, height(height)
	, value(value)
	{
	}

	RectangleLayerShape::RectangleLayerShape(int side, int value)
	: width(side)
	, height(side)
	, value(value)
	{
	}

	list<ShapePoint> RectangleLayerShape::getShapePoints()
	{
		list<ShapePoint> points;
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				points.push_back(ShapePoint(Point(x, y), value));
			}
		}

		return points;
	}

	CircleLayerShape::CircleLayerShape(int radius, int value)
	: radius(radius)
	, value(value)
	{
	}

	list<ShapePoint> CircleLayerShape::getShapePoints()
	{
		return list<ShapePoint>();
	}
}
