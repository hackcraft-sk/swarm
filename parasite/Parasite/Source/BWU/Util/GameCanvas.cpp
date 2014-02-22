#include "BWU/Util/GameCanvas.h"

#include <cstdio>
#include <cstdarg>

#include <sstream>

using namespace BWAPI;

namespace BWU
{
	GameCanvas::GameCanvas(Game &game, CoordinateType::Enum ctype)
	: game(game)
	, ctype(ctype)
	, CanvasBase()
	{
	}

	Canvas& GameCanvas::drawText(const char* format, ...)
	{
		game.setTextSize(textSize);

		int x = position.x();
		int y = position.y();

		char buffer[256];

		std::va_list arguments;

		va_start(arguments, format);
		vsprintf_s(buffer, sizeof(buffer), format, arguments);
		va_end(arguments);

		game.drawText(ctype, x, y, buffer);

		return *this;
	}

	Canvas& GameCanvas::drawText(std::string text)
	{
		drawText("%s", text.c_str());

		return *this;
	}

	Canvas& GameCanvas::drawBox(Dimension size)
	{
		int width = size.getWidth();
		int height = size.getHeight();

		drawBox(width, height);

		return *this;
	}

	Canvas& GameCanvas::drawBox(int width, int height)
	{
		int left = position.x();
		int top = position.y();
		int right = left + width;
		int bottom = top + height;

		game.drawBox(ctype, left, top, right, bottom, color, solid);

		return *this;
	}

	Canvas& GameCanvas::drawCircle(int radius)
	{
		int x = position.x();
		int y = position.y();

		game.drawCircle(ctype, x, y, radius, color, solid);

		return *this;
	}

	Canvas& GameCanvas::drawEllipse(int radiusX, int radiusY)
	{
		int x = position.x();
		int y = position.y();

		game.drawEllipse(ctype, x, y, radiusX, radiusY, color, solid);

		return *this;
	}

	Canvas& GameCanvas::drawDot()
	{
		int x = position.x();
		int y = position.y();

		game.drawDot(ctype, x, y, color);

		return *this;
	}

	Canvas& GameCanvas::drawLine(Position where)
	{
		int destX = where.x();
		int destY = where.y();

		drawLine(destX, destY);

		return *this;
	}

	Canvas& GameCanvas::drawLine(int destX, int destY)
	{
		int x = position.x();
		int y = position.y();

		game.drawLine(ctype, x, y, destX, destY, color);

		return *this;
	}

	Canvas& GameCanvas::drawTriangle(Position pointB, Position pointC)
	{
		int bX = pointB.x();
		int bY = pointB.y();

		int cX = pointC.x();
		int cY = pointC.y();

		drawTriangle(bX, bY, cX, cY);

		return *this;
	}

	Canvas& GameCanvas::drawTriangle(int bX, int bY, int cX, int cY)
	{
		int aX = position.x();
		int aY = position.y();

		game.drawTriangle(ctype, aX, aY, bX, bY, cX, cY, color, solid);

		return *this;
	}
}
