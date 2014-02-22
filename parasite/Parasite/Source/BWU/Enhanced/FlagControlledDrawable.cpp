#include "BWU/Enhanced/FlagControlledDrawable.h"

#include "BWU/Core/Canvas.h"
#include "BWU/Enhanced/Flags.h"

namespace BWU
{
	FlagControlledDrawable::FlagControlledDrawable(Flags &flags, Drawable *drawable, std::string flagName)
	: flags(flags)
	, drawable(drawable)
	, flagName(flagName)
	{
	}

	FlagControlledDrawable::~FlagControlledDrawable()
	{
		delete drawable;
	}

	void FlagControlledDrawable::draw(Canvas &canvas)
	{
		if (flags.isFlagEnabled(flagName))
		{
			drawable->draw(canvas);
		}
	}
}
