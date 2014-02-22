#pragma once

#include "BWU/Util/ViewDrawableBase.h"

#include <BWAPI.h>

#include "BWU/Core/Dimension.h"

namespace BWU
{
	class WindowBorderDrawable : public ViewDrawableBase
	{
		public:
			WindowBorderDrawable(BWAPI::Position position, Dimension size);
			virtual ~WindowBorderDrawable();

			virtual void draw(Canvas &canvas);

			BWAPI::Position getInternalPosition();
			BWU::Dimension getInternalSize();
	};
}
