<?php

use Nette\Application\Routers\RouteList,
	Nette\Application\Routers\Route;

class WrappedRouter implements Nette\Application\IRouter {

	/**
	 * @var Nette\Application\Routers\RouteList
	 */
	private $basicRouter;

	public function __construct($basicRouter) {
		$this->basicRouter = $basicRouter;
	}

	/**
	 * @param \Nette\Http\IRequest $httpRequest
	 * @return \Nette\Application\Request|NULL
	 */
	function match(Nette\Http\IRequest $httpRequest) {
		return $this->basicRouter->match($httpRequest);
	}

	function constructUrl(Nette\Application\Request $appRequest, Nette\Http\Url $refUrl) {
		return $this->basicRouter->constructUrl($appRequest, $refUrl);
	}

}

/**
 * Router factory.
 */
class RouterFactory
{

	/**
	 * @return Nette\Application\IRouter
	*/
	public function createBasicRouter()
	{
		$router = new RouteList();
		$router[] = new Route('index.php', 'Tournament:default', Route::ONE_WAY);
		$router[] = new Route('//<tournament>.mylifeforai.com/<presenter>/<action>[/<id>]', 'Tournament:default');
		$router[] = new Route('<presenter>/<action>[/<id>]', 'Tournament:default');
		return $router;
	}

	/**
	 * @return Nette\Application\IRouter
	 */
	public function createRouter() {
		return new WrappedRouter($this->createBasicRouter());
	}

}
