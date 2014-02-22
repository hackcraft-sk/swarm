#pragma once

#include <string>

#include "BWU/Core/Drawable.h"

namespace BWU
{
	class Canvas;
	class Flags;

	class FlagControlledDrawable : public Drawable
	{
		private:
			Flags &flags;
			Drawable *drawable;
			std::string flagName;
		public:
			FlagControlledDrawable(Flags &flags, Drawable *drawable, std::string flagName);
			~FlagControlledDrawable();

			virtual void draw(Canvas &canvas);
	};
}
