/**
 * AJAX Nette Framwork plugin for jQuery
 *
 * @copyright   Copyright (c) 2009 Jan Marek
 * @license     MIT
 * @link        http://nettephp.com/cs/extras/jquery-ajax
 * @version     0.2
 */

function NetteHandleSnippet(payload) {
	// redirect
	if (payload.redirect) {
		window.location.href = payload.redirect;
		return;
	}

	// snippets
	if (payload.snippets) {
		for (var i in payload.snippets) {
			$("#" + i).html(payload.snippets[i]);
		}
	}
}

jQuery.ajaxSetup({
	success: NetteHandleSnippet,
	dataType: "json"
});