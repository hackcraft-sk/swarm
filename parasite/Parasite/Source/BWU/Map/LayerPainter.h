#pragma once

#include <queue>
#include <list>

#include "BWU/Core/Point.h"
#include "BWU/Core/Rectangle.h"
#include "BWU/Map/ShapePoint.h"

namespace BWU
{
	class Layer;

	class LayerPainter
	{
		private:
			Layer &layer;

			void tryAddTile(Point position, int actualValue, std::queue<Point> &nextTiles);
		public:
			LayerPainter(Layer &layer);

			template <class S>
			void paintShape(S shape);

			template <class S, class M>
			void paintShape(S shape, M modifier);

			void flood(Point center, int distance);

			void fillCircle(Point center, int radius, int value);

			void paintLine(Point from, Point to, int value);
			void paintPoint(Point point, int value);

			void paintRectangle(Rectangle rectangle, int value);
			void fillRectangle(Rectangle rectangle, int value);
	};

	template <class S>
	void LayerPainter::paintShape(S shape)
	{
		std::list<ShapePoint> cells = shape.getShapePoints();
		std::list<ShapePoint>::iterator it;

		for (it = cells.begin(); it != cells.end(); it++)
		{
			ShapePoint cell = *it;

			Point position = cell.getOffset();
			int newValue = cell.getValue();

			layer.setValue(position, newValue);
		}
	}

	template <class S, class M>
	void LayerPainter::paintShape(S shape, M modifier)
	{
		std::list<ShapePoint> cells = shape.getShapePoints();
		std::list<ShapePoint>::iterator it;

		for (it = cells.begin(); it != cells.end(); it++)
		{
			ShapePoint cell = *it;

			Point position = cell.getOffset();
			int newValue = cell.getValue();
			int oldValue = layer.getValue(position);

			int finalValue = modifier(newValue, oldValue);
			layer.setValue(position, finalValue);
		}
	}
}
