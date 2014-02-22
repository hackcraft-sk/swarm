#pragma once

#include "BWU/Map/Layer.h"

namespace BWU
{
	class BitmapLayer : public Layer
	{
		private:
			Dimension size;
			int *matrix;

			int positionToIndex(Point position) const;
		public:
			BitmapLayer(Dimension size, int initialValue);
			BitmapLayer(Layer &layer);
			~BitmapLayer();

			Dimension getSize() const;

			void setValue(Point position, int value);
			int getValue(Point position) const;

			bool isValid(Point position) const;
	};
}
