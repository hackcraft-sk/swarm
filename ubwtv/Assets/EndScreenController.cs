using UnityEngine;
using System.Collections;

public class EndScreenController : ScreenController {

	public UnityEngine.UI.Text winnerText;
	public UnityEngine.UI.Text player1Text;
	public UnityEngine.UI.Text player2Text;

	protected override void OnActivation() {
		gameObject.SetActive (true);
		int matchId = eventInfo.matchId;
		StartCoroutine (DownloadMatchInfo (matchId));
	}

	protected override void OnDeactivation() {
		gameObject.SetActive (false);
		winnerText.text = "";
		player1Text.text = "";
		player2Text.text = "";
	}

	protected override void OnMatchInfoDownloaded (MatchInfo matchInfo) {
		if (matchInfo.state != "OK") {
			int matchId = eventInfo.matchId;
			StartCoroutine (DownloadMatchInfo (matchId));
			return;
		}
			
		Bot winner = matchInfo.GetWinner();

		if (winner == null) {
			winnerText.text = "Draw!";

			Bot[] bots = matchInfo.bots;

			player1Text.text = createText (bots[0].name, -1);
			player2Text.text = createText (bots[1].name, -1);
		} else {
			winnerText.text = winner.name + " wins!";

			Bot looser = matchInfo.GetLooser ();

			player1Text.text = createText (winner.name, 3);
			player2Text.text = createText (looser.name, 0);
		}
	}

	private string createText(string botName, int pts) {
		string sign = pts > 0 ? "+" : "";
		return botName + " " + sign + pts + " pts";
	}

}
