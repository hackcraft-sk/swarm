#pragma once

#include <BWAPI.h>

#include "BWU/Util/CanvasBase.h"

namespace BWU
{
	class GameCanvas : public CanvasBase
	{
		private:
			BWAPI::Game &game;
			BWAPI::CoordinateType::Enum ctype;
		public:
			GameCanvas(BWAPI::Game &game, BWAPI::CoordinateType::Enum ctype);

			Canvas& drawText(const char *format, ...);
			Canvas& drawText(std::string text);
			Canvas& drawBox(Dimension size);
			Canvas& drawBox(int width, int height);
			Canvas& drawCircle(int radius);
			Canvas& drawEllipse(int radiusX, int radiusY);
			Canvas& drawDot();
			Canvas& drawLine(BWAPI::Position where);
			Canvas& drawLine(int destX, int destY);
			Canvas& drawTriangle(BWAPI::Position pointB, BWAPI::Position pointC);
			Canvas& drawTriangle(int bX, int bY, int cX, int cY);
	};
}
