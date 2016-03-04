
using UnityEngine;
using System.Collections;

public class OuterLevelGradientColorChangeEffect : ContinuousGraphicalEffect {
	
	public bool affectTopSky = false;
	public Color32 topSkyColor;
	
	public bool affectBottomSky = false;
	public Color32 bottomSkyColor;

	public bool affectTopGround = false;
	public Color32 topGroundColor;
	
	public bool affectBottomGround = false;
	public Color32 bottomGroundColor;
	
	private Color32 topGroundStart;
	private Color32 bottomGroundStart;
	private Color32 topSkyStart;
	private Color32 bottomSkyStart;
	
	private GradientBackground backgroundScript;
	
	protected override void Prepare() {
		CheckAtLeastOneColorActive ();
		LoadTargetScript ();
		
		SetStartColors ();
	}
	
	protected override void PerformContinuousChange() {
		if (affectTopGround) {
			Color32 newColor = Color32.Lerp (topGroundStart, topGroundColor, currentStatus);
			backgroundScript.topColorGround = newColor;
		}
		
		if (affectBottomGround) {
			Color32 newColor = Color32.Lerp (bottomGroundStart, bottomGroundColor, currentStatus);
			backgroundScript.bottomColorGround = newColor;
		}
		
		if (affectTopSky) {
			Color32 newColor = Color32.Lerp (topSkyStart, topSkyColor, currentStatus);
			backgroundScript.topColorSky = newColor;
		}
		
		if (affectBottomSky) {
			Color32 newColor = Color32.Lerp (bottomSkyStart, bottomSkyColor, currentStatus);
			backgroundScript.bottomColorSky = newColor;
		}
	}
	
	protected override void Cleanup() {
		
	}
	
	private void CheckAtLeastOneColorActive() {
		if (!affectTopGround && !affectTopSky && !affectBottomGround && !affectBottomSky) {
			Debug.LogWarning("Background gradient effect not set to do anything: " + gameObject);
		}
	}
	
	private void LoadTargetScript() {
		backgroundScript = GetTargetObject ().GetComponent<GradientBackground> ();
		if (backgroundScript == null) {
			Debug.LogError("Invalid target object. No GradientBackground found: " + gameObject + ", " + GetTargetObject());
		}
	}
	
	private void SetStartColors() {
		topGroundStart = backgroundScript.topColorGround;
		bottomGroundStart = backgroundScript.bottomColorGround;
		topSkyStart = backgroundScript.topColorSky;
		bottomSkyStart = backgroundScript.bottomColorSky;
	}
}


