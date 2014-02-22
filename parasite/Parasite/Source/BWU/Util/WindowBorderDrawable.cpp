#include "BWU/Util/WindowBorderDrawable.h"

#include <BWAPI.h>

#include "BWU/Core/Dimension.h"
#include "BWU/Core/Canvas.h"

using namespace BWAPI;
using BWU::Dimension;

namespace BWU
{
	WindowBorderDrawable::WindowBorderDrawable(BWAPI::Position position, Dimension size)
	: ViewDrawableBase(size, position)
	{
	}

	WindowBorderDrawable::~WindowBorderDrawable()
	{
	}

	void WindowBorderDrawable::draw(Canvas& canvas)
	{
		canvas
		.setSolid(true)

		.setPosition(position + Position(1, 1))
		.setColor(Colors::Black)
		.drawBox(size.getWidth() - 2, size.getHeight() - 2)
		.setColor(Colors::Red)

		.setPosition(position + Position(1, 0))
		.drawLine(position + Position(size.getWidth() - 1, 0))

		.setPosition(position + Position(1, size.getHeight() - 1))
		.drawLine(position + Position(size.getWidth() - 1, size.getHeight() - 1))

		.setPosition(position + Position(0, 1))
		.drawLine(position + Position(0, size.getHeight() - 1))

		.setPosition(position + Position(size.getWidth() - 1, 1))
		.drawLine(position + Position(size.getWidth() - 1, size.getHeight() - 1))
		;
	}

	Position WindowBorderDrawable::getInternalPosition()
	{
		return position + Position(5, 5);
	}

	Dimension WindowBorderDrawable::getInternalSize()
	{
		return Dimension(size.getWidth() - 10, size.getHeight() - 10);
	}
}
