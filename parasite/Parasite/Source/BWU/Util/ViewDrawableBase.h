#pragma once

#include "BWU/Core/ViewDrawable.h"

namespace BWU
{
	class ViewDrawableBase : public ViewDrawable
	{
		protected:
			BWAPI::Position position;
			Dimension size;
		public:
			ViewDrawableBase(Dimension size);
			ViewDrawableBase(Dimension size, BWAPI::Position position);

			virtual BWAPI::Position getPosition();
			virtual void setPosition(BWAPI::Position position);

			virtual Dimension getSize();
	};
}
