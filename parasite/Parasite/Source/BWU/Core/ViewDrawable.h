#pragma once

#include "BWU/Core/Drawable.h"

#include <BWAPI.h>

#include "BWU/Core/Dimension.h"

namespace BWU
{
	class ViewDrawable : public Drawable
	{
		public:
			virtual ~ViewDrawable() {};

			virtual BWAPI::Position getPosition() = 0;
			virtual void setPosition(BWAPI::Position position) = 0;

			virtual Dimension getSize() = 0;
	};
}
