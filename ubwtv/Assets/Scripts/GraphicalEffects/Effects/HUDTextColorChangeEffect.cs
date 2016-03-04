
using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class HUDTextColorChangeEffect : ContinuousGraphicalEffect {
	
	public Color32 color;
	
	public bool customStartColor = false;
	public Color32 startColor;

	private Text targetText;
	
	protected override void Prepare() {
		targetText = GetTargetObject ().GetComponent<Text> ();
		if (!customStartColor) {
			SetStartColor();
		}
	}
	
	protected override void PerformContinuousChange() {
		Color32 newColor = Color32.Lerp (startColor, color, currentStatus);
		
		targetText.color = newColor;
	}
	
	protected override void Cleanup() {
		
	}
	
	private void SetStartColor() {
		startColor = targetText.color;
	}
}
