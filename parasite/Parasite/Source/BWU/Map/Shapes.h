#pragma once

#include <list>

#include "BWU/Map/ShapePoint.h"

namespace BWU
{
	class RectangleLayerShape
	{
		private:
			int width, height;
			int value;
		public:
			RectangleLayerShape(int width, int height, int value);
			RectangleLayerShape(int side, int value);

			std::list<ShapePoint> getShapePoints();
	};

	class CircleLayerShape
	{
		private:
			int radius;
			int value;
		public:
			CircleLayerShape(int radius, int value);

			std::list<ShapePoint> getShapePoints();
	};

	/**
	USE CASE

	Layer layer;

	BuildingShapePainter buildingPainter;
	Unit *barracks;

	buildingPainter.paintBuilding(layer, barracks);

	 */
}
