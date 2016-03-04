
using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class HUDTextTextChangeEffect : GraphicalEffect {
	
	public string newText;

	public override void PerformChange() {
		Text target = GetTargetObject ().GetComponent<Text> ();
		target.text = newText;
	}
}

