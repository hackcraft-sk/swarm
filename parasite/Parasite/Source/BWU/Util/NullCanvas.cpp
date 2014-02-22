#include "NullCanvas.h"

namespace BWU
{
	Canvas& NullCanvas::setPosition(BWAPI::Position position)
	{
		return *this;
	}

	Canvas& NullCanvas::setPosition(int x, int y)
	{
		return *this;
	}

	Canvas& NullCanvas::setColor(BWAPI::Color color)
	{
		return *this;
	}

	Canvas& NullCanvas::setSolid(bool solid)
	{
		return *this;
	}

	Canvas& NullCanvas::setTextSize(TextSize::Enum size)
	{
		return *this;
	}

	Canvas& NullCanvas::drawText(const char* format, ...)
	{
		return *this;
	}

	Canvas& NullCanvas::drawText(std::string text)
	{
		return *this;
	}

	Canvas& NullCanvas::drawBox(Dimension size)
	{
		return *this;
	}

	Canvas& NullCanvas::drawBox(int width, int height)
	{
		return *this;
	}

	Canvas& NullCanvas::drawCircle(int radius)
	{
		return *this;
	}

	Canvas& NullCanvas::drawEllipse(int radiusX, int radiusY)
	{
		return *this;
	}

	Canvas& NullCanvas::drawDot()
	{
		return *this;
	}

	Canvas& NullCanvas::drawLine(BWAPI::Position where)
	{
		return *this;
	}

	Canvas& NullCanvas::drawLine(int destX, int destY)
	{
		return *this;
	}

	Canvas& NullCanvas::drawTriangle(BWAPI::Position pointB, BWAPI::Position pointC)
	{
		return *this;
	}

	Canvas& NullCanvas::drawTriangle(int bX, int bY, int cX, int cY)
	{
		return *this;
	}
}
