#pragma once

#include <string>

#include <BWAPI.h>

#include "BWU/Core/Dimension.h"
#include "BWU/Core/TextSize.h"

namespace BWU
{
	class Canvas
	{
		public:
			virtual ~Canvas() {};

			virtual Canvas& setPosition(BWAPI::Position position) = 0;
			virtual Canvas& setPosition(int x, int y) = 0;
			virtual Canvas& setColor(BWAPI::Color color) = 0;
			virtual Canvas& setSolid(bool solid) = 0;
			virtual Canvas& setTextSize(TextSize::Enum size) = 0;

			virtual void reset() = 0;

			virtual Canvas& drawText(const char *format, ...) = 0;
			virtual Canvas& drawText(std::string text) = 0;
			virtual Canvas& drawBox(Dimension size) = 0;
			virtual Canvas& drawBox(int width, int height) = 0;
			virtual Canvas& drawCircle(int radius) = 0;
			virtual Canvas& drawEllipse(int radiusX, int radiusY) = 0;
			virtual Canvas& drawDot() = 0;
			virtual Canvas& drawLine(BWAPI::Position where) = 0;
			virtual Canvas& drawLine(int destX, int destY) = 0;
			virtual Canvas& drawTriangle(BWAPI::Position pointB, BWAPI::Position pointC) = 0;
			virtual Canvas& drawTriangle(int bX, int bY, int cX, int cY) = 0;
	};
}
