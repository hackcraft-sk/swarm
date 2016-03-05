using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class RunScreenController : ScreenController {

	public Text mainPlayerName;
	public Text secondaryPlayerName;

	protected override void OnActivation() {
        gameObject.SetActive(true);

		mainPlayerName.text = "";
		secondaryPlayerName.text = "";

		int matchId = eventInfo.matchId;
		StartCoroutine(DownloadMatchInfo(matchId));
    }

	protected override void OnDeactivation() {
	}

	protected override void OnMatchInfoDownloaded(MatchInfo matchInfo) {
		mainPlayerName.text = matchInfo.bots [0].name;
		secondaryPlayerName.text = matchInfo.bots [1].name;
	}	

}
