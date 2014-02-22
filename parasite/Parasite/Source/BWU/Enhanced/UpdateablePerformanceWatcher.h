#pragma once

#include "BWU/Enhanced/PerformanceWatcher.h"

namespace BWU
{
	class UpdateablePerformanceWatcher : public PerformanceWatcher
	{
		private:
			Updateable &updateable;
		public:
			UpdateablePerformanceWatcher(Updateable &updateable);

			virtual void update();
	};
}
