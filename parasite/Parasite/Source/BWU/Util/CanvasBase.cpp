#include "BWU/Util/CanvasBase.h"

#include <BWAPI.h>

namespace BWU
{
	CanvasBase::CanvasBase()
	: position(BWAPI::Position(0, 0))
	, color(BWAPI::Colors::White)
	, solid(false)
	, textSize(TextSize::NORMAL)
	{
	}

	Canvas& CanvasBase::setPosition(BWAPI::Position position)
	{
		this->position = position;
		return *this;
	}

	Canvas& CanvasBase::setPosition(int x, int y)
	{
		this->position = BWAPI::Position(x, y);
		return *this;
	}

	Canvas& CanvasBase::setColor(BWAPI::Color color)
	{
		this->color = color;
		return *this;
	}

	Canvas& CanvasBase::setSolid(bool solid)
	{
		this->solid = solid;
		return *this;
	}

	Canvas& CanvasBase::setTextSize(TextSize::Enum size)
	{
		this->textSize = size;
		return *this;
	}

	void CanvasBase::reset()
	{
		this->position = BWAPI::Position(0, 0);
		this->color = BWAPI::Colors::White;
		this->solid = false;
		this->textSize = TextSize::NORMAL;
	}
}
