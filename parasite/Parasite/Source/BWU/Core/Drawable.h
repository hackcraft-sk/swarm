#pragma once

namespace BWU
{
	class Canvas;

	class Drawable
	{
		public:
			virtual ~Drawable() {};
			virtual void draw(Canvas &canvas) = 0;
	};
}
