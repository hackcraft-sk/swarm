#include "BWU/Util/GameMap.h"

namespace BWU
{
	Dimension GameMap::createSize(BWAPI::Game &game)
	{
		int width = game.mapWidth() * TILE_SIZE;
		int height = game.mapHeight() * TILE_SIZE;

		return Dimension(width, height);
	}

	Dimension GameMap::createTileSize(BWAPI::Game &game)
	{
		int tileWidth = game.mapWidth();
		int tileHeight = game.mapHeight();

		return Dimension(tileWidth, tileHeight);
	}

	GameMap::GameMap(BWAPI::Game &game)
	: game(game)
	, size(createSize(game))
	, tileSize(createTileSize(game))
	, buildableLayer(BroodWarMapLayer<BroodWarMapSelectors::BuildableSelector>(game))
	{
	}

	Dimension GameMap::getPixelSize() const
	{
		return size;
	}

	Dimension GameMap::getTileSize() const
	{
		return tileSize;
	}

	std::string GameMap::getName() const
	{
		return game.mapName();
	}

	std::string GameMap::getHash() const
	{
		return game.mapHash();
	}

	std::string GameMap::getFileName() const
	{
		return game.mapFileName();
	}

	std::string GameMap::getPathName() const
	{
		return game.mapPathName();
	}

	void GameMap::ping(BWAPI::Position& position) const
	{
		game.pingMinimap(position);
	}

	void GameMap::ping(int x, int y) const
	{
		game.pingMinimap(x, y);
	}

	ReadonlyLayer &GameMap::getBuildableLayer()
	{
		return buildableLayer;
	}
}
