#include "BWU/Map/LayerPainter.h"

#include "BWU/Core/Dimension.h"
#include "BWU/Map/Layer.h"

using std::queue;

namespace BWU
{
	BWU::LayerPainter::LayerPainter(Layer &layer)
	: layer(layer)
	{
	}

	void BWU::LayerPainter::flood(Point center, int distance)
	{
		if (layer.getValue(center) >= distance)
		{
			return;
		}

		layer.setValue(center, distance);

		queue<Point> nextPositions;
		nextPositions.push(center);

		while (!nextPositions.empty())
		{
			Point position = nextPositions.front();
			nextPositions.pop();

			int newValue = layer.getValue(position) - 1;

			tryAddTile(position + Point( 1,  0), newValue, nextPositions);
			tryAddTile(position + Point(-1,  0), newValue, nextPositions);
			tryAddTile(position + Point( 0,  1), newValue, nextPositions);
			tryAddTile(position + Point( 0, -1), newValue, nextPositions);
		}
	}

	void LayerPainter::tryAddTile(Point position, int actualValue, queue<Point> &nextTiles)
	{
		int value = layer.getValue(position);

		if (value > actualValue)
		{
			return;
		}

		if (layer.isValid(position))
		{
			layer.setValue(position, actualValue);
		}

		nextTiles.push(position);
	}

	void LayerPainter::fillCircle(Point center, int radius, int value)
	{
		// Midpoint / Bresenham's circle algorithm

		int x = radius;
		int y = 0;
		int radiusError = 1 - x;

		while(x >= y)
		{
			paintLine(Point( x + center.getX(),  y + center.getY()), Point(-x + center.getX(),  y + center.getY()), value);
			paintLine(Point( y + center.getX(),  x + center.getY()), Point(-y + center.getX(),  x + center.getY()), value);
			paintLine(Point(-x + center.getX(), -y + center.getY()), Point( x + center.getX(), -y + center.getY()), value);
			paintLine(Point(-y + center.getX(), -x + center.getY()), Point( y + center.getX(), -x + center.getY()), value);

			y++;

			if(radiusError < 0)
			{
				radiusError += 2*y + 1;
			}
			else
			{
				x--;
				radiusError += 2 * (y - x + 1);
			}
		}
	}

	void LayerPainter::paintLine(Point from, Point to, int value)
	{
		// Bresenham's line algorithm

		int x0 = from.getX();
		int y0 = from.getY();

		int x1 = to.getX();
		int y1 = to.getY();

		int dx = abs(x1 - x0);
		int sx = x0 < x1 ? 1 : -1;

		int dy = abs(y1 - y0);
		int sy = y0 < y1 ? 1 : -1;

		int err = (dx > dy ? dx : -dy) / 2;
		int e2;

		for(;;)
		{
			Point pos(x0, y0);

			if (layer.isValid(pos))
			{
				layer.setValue(Point(x0, y0), value);
			}

			if (x0 == x1 && y0 == y1)
			{
				break;
			}

			e2 = err;

			if (e2 > -dx)
			{
				err -= dy; x0 += sx;
			}

			if (e2 < dy)
			{
				err += dx; y0 += sy;
			}
		}
	}

	void BWU::LayerPainter::paintPoint(Point point, int value)
	{
		layer.setValue(point, value);
	}
}

void BWU::LayerPainter::paintRectangle(Rectangle rectangle, int value)
{
	Point position = rectangle.getPosition();
	Dimension size = rectangle.getSize();

	int xFrom = position.getX();
	int xTo = xFrom + size.getWidth();

	int yFrom = position.getY();
	int yTo = yFrom + size.getHeight();

	for (int i = xFrom; i < xTo; i++)
	{
		layer.setValue(Point(i, yFrom), value);
		layer.setValue(Point(i, yTo - 1), value);
	}

	for (int i = yFrom; i < yTo; i++)
	{
		layer.setValue(Point(i, xFrom), value);
		layer.setValue(Point(i, xTo - 1), value);
	}
}

void BWU::LayerPainter::fillRectangle(Rectangle rectangle, int value)
{
	Point position = rectangle.getPosition();
	Dimension size = rectangle.getSize();

	int xFrom = position.getX();
	int xTo = xFrom + size.getWidth();

	int yFrom = position.getY();
	int yTo = yFrom + size.getHeight();

	for (int i = xFrom; i < xTo; i++)
	{
		for (int j = yFrom; j < yTo; j++)
		{
			layer.setValue(Point(i, j), value);
		}
	}
}
