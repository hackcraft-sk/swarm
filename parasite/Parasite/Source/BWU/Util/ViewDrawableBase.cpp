#include "ViewDrawableBase.h"

using BWAPI::Position;

namespace BWU
{
	ViewDrawableBase::ViewDrawableBase(Dimension size)
	: size(size)
	, position(Position(0, 0))
	{
	}

	ViewDrawableBase::ViewDrawableBase(Dimension size, Position position)
	: size(size)
	, position(position)
	{
	}

	BWAPI::Position ViewDrawableBase::getPosition()
	{
		return position;
	}

	void ViewDrawableBase::setPosition(Position position)
	{
		this->position = position;
	}

	Dimension ViewDrawableBase::getSize()
	{
		return size;
	}
}
