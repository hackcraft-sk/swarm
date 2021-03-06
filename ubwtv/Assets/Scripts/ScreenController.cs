﻿using UnityEngine;
using System.Collections;

public abstract class ScreenController : MonoBehaviour {

	public SceneManager sceneManager;

	public EventType eventType;

    public GraphicalEffect deactivationEffect;

    public GraphicalEffect activationEffect;

	protected EventInfo eventInfo;

	public void MatchStateChanged(EventInfo eventInfo) {
		EventType receivedEventType = eventInfo.eventType;
		if (gameObject.activeInHierarchy && receivedEventType != eventType) {
            if (deactivationEffect != null)
            {
                deactivationEffect.EnqueueToScheduler();
            }
            OnDeactivation ();
		} else if (!gameObject.activeInHierarchy && receivedEventType == eventType) {
			this.eventInfo = eventInfo;
            if (activationEffect != null)
            {
                activationEffect.EnqueueToScheduler();
            }
            OnActivation ();
        }
	}

	protected IEnumerator DownloadMatchInfo(int matchId) {
		string json = "{\"matchId\": " + matchId + "}";
		json = WWW.EscapeURL (json);

		Config config = sceneManager.config;
		string url = config.web + "/json/get-match-info?content=" + json;
		WWW www = new WWW(url);

		yield return www;

		if (www.error != null) {
			Debug.Log ("Web error: " + www.error);
		} else {
			MatchInfo matchInfo = JsonUtility.FromJson<MatchInfo>(www.text);

			Debug.Log ("Downloaded match info.");

			OnMatchInfoDownloaded (matchInfo);
		}
	}

	protected IEnumerator DownloadUpcomingMatches(int tournamentId) {
		string json = "{\"tournamentId\": " + tournamentId + "}";
		json = WWW.EscapeURL (json);

		Config config = sceneManager.config;
		string url = config.web + "/json/get-upcoming-matches?content=" + json;
		WWW www = new WWW(url);

		yield return www;

		if (www.error != null) {
			Debug.Log ("Web error: " + www.error);
		} else {
			UpcomingMatches upcomingMatches = JsonUtility.FromJson<UpcomingMatches>(www.text);

			Debug.Log ("Downloaded upcoming matches.");

			OnUpcomingMatchesDownloaded (upcomingMatches);
		}
	}

	protected virtual void OnMatchInfoDownloaded (MatchInfo matchInfo) {
	}

	protected virtual void OnUpcomingMatchesDownloaded(UpcomingMatches upcomingMatches) {
	}

	protected abstract void OnActivation ();

	protected abstract void OnDeactivation ();

}
