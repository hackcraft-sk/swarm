#pragma once

#include "BWU/Core/Drawable.h"
#include "BWU/Core/Point.h"

namespace BWU
{
	class Layer;
	class Canvas;

	class LayerDrawable : public Drawable
	{
		private:
			Layer &layer;
		protected:
			virtual void drawCell(Canvas &canvas, Point position, int value) = 0;
		public:
			LayerDrawable(Layer &layer);

			void draw(Canvas &canvas);
	};
}
