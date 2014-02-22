#pragma once

#include <time.h>

#include "BWU/Core/Updateable.h"
#include "BWU/Core/Drawable.h"

namespace BWU
{
	class Canvas;

	class PerformanceWatcher
	{
		private:
			clock_t begin, end;
			float delta;
		protected:
			void saveBeginning();
			void saveEnd();
			float calcDelta();
		public:
			virtual ~PerformanceWatcher() {};

			float getDelta();
	};
}
