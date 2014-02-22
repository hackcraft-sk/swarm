#include "BWU/Enhanced/DrawablePerformanceWatcher.h"

namespace BWU
{
	BWU::DrawablePerformanceWatcher::DrawablePerformanceWatcher(Drawable& drawable)
	: drawable(drawable)
	{
	}

	void BWU::DrawablePerformanceWatcher::draw(Canvas& canvas)
	{
		saveBeginning();
		drawable.draw(canvas);
		saveEnd();
	}
}
