#pragma once

#include "BWU/Enhanced/PerformanceWatcher.h"

namespace BWU
{
	class Drawable;
	class Canvas;

	class DrawablePerformanceWatcher : public PerformanceWatcher
	{
		private:
			Drawable &drawable;
		public:
			DrawablePerformanceWatcher(Drawable &drawable);

			virtual void draw(Canvas &canvas);
	};
}
