#include "PerformanceWatcher.h"

#include <time.h>

#include <BWAPI.h>

#include "BWU/Core/Updateable.h"
#include "BWU/Core/Drawable.h"
#include "BWU/Core/Canvas.h"

namespace BWU
{
	void PerformanceWatcher::saveBeginning()
	{
		begin = clock();
	}

	void PerformanceWatcher::saveEnd()
	{
		end = clock();
	}

	float PerformanceWatcher::calcDelta()
	{
		return (end - begin) / CLOCKS_PER_SEC;
	}
}
