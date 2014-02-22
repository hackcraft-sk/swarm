#include "BWU/Enhanced/UpdateablePerformanceWatcher.h"

namespace BWU
{
	UpdateablePerformanceWatcher::UpdateablePerformanceWatcher(Updateable& updateable)
	: updateable(updateable)
	{
	}

	void UpdateablePerformanceWatcher::update()
	{
		saveBeginning();
		updateable.update();
		saveEnd();
	}
}
