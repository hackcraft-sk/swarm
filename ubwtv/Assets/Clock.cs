using UnityEngine;
using System;
using System.Collections;

public class Clock : MonoBehaviour {

	private UnityEngine.UI.Text clockText;

	void Start () {
		clockText = GetComponent<UnityEngine.UI.Text> ();
	}
	
	// Update is called once per frame
	void Update () {
		DateTime utcTime = DateTime.UtcNow;
		string utcTimeString = String.Format("{0:D2}:{1:D2}:{2:D2} UTC", utcTime.Hour, utcTime.Minute, utcTime.Second);
		clockText.text = utcTimeString;
	}
}
