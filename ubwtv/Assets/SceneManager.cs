using UnityEngine;
using System;
using System.Runtime.CompilerServices;
using System.Collections;
using System.IO;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using System.Threading;

public class SceneManager : MonoBehaviour {

	public Config config;

	private Thread workerThread;

	private EventInfo receivedEventInfo;

	public ScreenController[] screenControllers = {};

	void Start() {
		Screen.SetResolution (640, 480, false, 60);

		LoadConfig ();

		if (config.mock) {
			Debug.Log ("Runnign tests.");
			StartCoroutine (Test ());
		} else {
			Debug.Log ("Starting network thread.");
			Thread thread = new Thread (NetworkWorker);
			thread.IsBackground = true;
			thread.Start();
		}
	}

	void Update() {
		if (receivedEventInfo != null) {
			BroadcastStateChange (receivedEventInfo);
			receivedEventInfo = null;
		}
	}

	void NetworkWorker() {
		while (true) {
			try {
				string address = config.address;
				int port = config.port;

				TcpClient tcpClient = new TcpClient (address, port);
				tcpClient.ReceiveTimeout = 5 * 60 * 1000;

				Stream networkStream = tcpClient.GetStream();
				BinaryReader reader = new BinaryReader(networkStream);

				Debug.Log ("Connected to overlord.");

				while (true) {
					Debug.Log("Waiting for message...");

					byte[] eventBytes = reader.ReadBytes(4);
					byte[] matchIdBytes = reader.ReadBytes(4);

					if (BitConverter.IsLittleEndian) {
						Array.Reverse (eventBytes);
						Array.Reverse (matchIdBytes);
					}

					EventType eventType = (EventType)BitConverter.ToInt32 (eventBytes, 0);

					if (eventType == EventType.Ping) {
						Debug.Log ("Received ping.");
					} else {
						int matchId = BitConverter.ToInt32 (matchIdBytes, 0);

						Debug.Log ("Received message: " + eventType + " " + matchId);

						EventInfo eventInfo = new EventInfo(eventType, matchId);
						OnMessageReceived(eventInfo);
					}
				}
			} catch (Exception ex) {
				Debug.Log ("Network exception: " + ex);
			} finally {
				Thread.Sleep (5000);
			}
		}
	}

	[MethodImpl(MethodImplOptions.Synchronized)]
	private void OnMessageReceived(EventInfo eventInfo) {
		this.receivedEventInfo = eventInfo;
	}

	private IEnumerator Test() {
		
		while (true) {
			EventType eventType = (EventType)UnityEngine.Random.Range (1, 4);
			int matchId = config.mockMatchId;
			receivedEventInfo = new EventInfo (eventType, matchId);
			yield return new WaitForSeconds (3);
		}
	}

	private void LoadConfig() {
		try {
			string rawConfig = File.ReadAllText ("bwtv-config.json");
			config = JsonUtility.FromJson<Config> (rawConfig);
		} catch (Exception ex) {
			Console.WriteLine (ex.ToString());
		}
	}

	private void BroadcastStateChange(EventInfo eventInfo) {
		Debug.Log ("Broadcasting state change.");

		foreach (ScreenController screenController in screenControllers) {
			screenController.MatchStateChanged (eventInfo);
		}
	}
}
