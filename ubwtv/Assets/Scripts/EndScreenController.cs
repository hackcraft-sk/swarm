using UnityEngine;
using System.Collections;

public class EndScreenController : ScreenController {

	public UnityEngine.UI.Text winnerText;
	public UnityEngine.UI.Text player1Text;
	public UnityEngine.UI.Text player2Text;

	public UnityEngine.UI.Text nextLeftPlayers;
	public UnityEngine.UI.Text nextRightPlayers;

	protected override void OnActivation() {
        gameObject.SetActive(true);

		winnerText.text = "...";
		player1Text.text = "...";
		player2Text.text = "...";

		nextLeftPlayers.text = "...";
		nextRightPlayers.text = "...";

		int matchId = eventInfo.matchId;
		StartCoroutine (DownloadMatchInfo (matchId));
	}

	protected override void OnDeactivation() {
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

		int tournamentId = matchInfo.tournamentId;
		StartCoroutine(DownloadUpcomingMatches(tournamentId));
	}

	protected override void OnUpcomingMatchesDownloaded(UpcomingMatches upcomingMatches) {
		Match[] matches = upcomingMatches.matches;

		nextLeftPlayers.text = "";
		nextRightPlayers.text = "";

		for (int i = 0; i < 3 && i < matches.Length; i++) {
			Match match = matches [i];
			string botNameLeft = match.bots [0];
			string botNameRight = match.bots [1];

			if (i > 0) {
				nextLeftPlayers.text += "\n";
				nextRightPlayers.text += "\n";
			}
			nextLeftPlayers.text += botNameLeft;
			nextRightPlayers.text += botNameRight;
		}
	}

	private string createText(string botName, int pts) {
		string sign = pts >= 0 ? "+" : "";
		return botName + "\n" + sign + pts + " pts";
	}

}
