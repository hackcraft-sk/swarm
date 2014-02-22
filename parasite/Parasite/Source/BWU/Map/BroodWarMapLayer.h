#pragma once

#include "BWU/Map/ReadonlyLayer.h"

#include <BWAPI.h>

namespace BWU
{
	template <class S>
	class BroodWarMapLayer : public ReadonlyLayer
	{
		private:
			BWAPI::Game &game;
			S selector;
		public:
			BroodWarMapLayer(BWAPI::Game &game);

			virtual Dimension getSize() const;

			virtual int getValue(Point position) const;

			virtual bool isValid(Point position) const;
	};

	template <class S>
	BroodWarMapLayer<S>::BroodWarMapLayer(BWAPI::Game &game)
	: game(game)
	{
	}

	template <class S>
	Dimension BroodWarMapLayer<S>::getSize() const
	{
		return Dimension(game.mapWidth(), game.mapHeight());
	}

	template <class S>
	int BroodWarMapLayer<S>::getValue(Point position) const
	{
		return selector(game, position);
	}

	template <class S>
	bool BroodWarMapLayer<S>::isValid(Point position) const
	{
		if (position.getX() < 0 || position.getX() > game.mapWidth())
		{
			return false;
		}

		if (position.getY() < 0 || position.getY() > game.mapHeight())
		{
			return false;
		}

		return true;
	}

	namespace BroodWarMapSelectors
	{
		class BuildableSelector
		{
			public:
				int operator()(BWAPI::Game &game, Point position) const
				{
					int x = position.getX();
					int y = position.getY();

					return game.isBuildable(x, y, false);
				}
		};
	}
}
