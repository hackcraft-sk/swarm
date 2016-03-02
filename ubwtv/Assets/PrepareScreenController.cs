using UnityEngine;
using System.Collections;

public class PrepareScreenController : ScreenController {

	public UnityEngine.UI.Text player1Text;
	public UnityEngine.UI.Text player2Text;

	void OnEnable() {
		int matchId = eventInfo.matchId;
		StartCoroutine (DownloadMatchInfo (matchId));
	}

	void OnDisable() {
		player1Text.text = "";
		player2Text.text = "";
	}

	protected override void OnMatchInfoDownloaded(MatchInfo matchInfo) {
		player1Text.text = matchInfo.bots [0].name;
		player2Text.text = matchInfo.bots [1].name;
	}

}
