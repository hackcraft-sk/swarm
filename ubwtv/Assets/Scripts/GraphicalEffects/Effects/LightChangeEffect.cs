using UnityEngine;
using System.Collections;

public class LightChangeEffect : ContinuousGraphicalEffect {

	public bool affectColor;
	public Color32[] newColors;
	public bool affectIntensity;
	public float[] newIntensities;
	
	private Light[] lights;
	private Color32[] oldColors;
	private float[] oldIntensities;
	
	protected override void Prepare() {
		FindLights();
	}
	
	protected override void PerformContinuousChange() {
		for (int i = 0; i < lights.Length; i++) {
			Light l = lights[i];
			
			if (affectColor) {
				l.color = Color32.Lerp (oldColors[i], newColors[i % newColors.Length], currentStatus);
			}
			if (affectIntensity) {
				l.intensity = (oldIntensities[i] * (1 - currentStatus)) + (newIntensities[i % newIntensities.Length] * currentStatus);
			}
		}
	}
	
	protected override void Cleanup() {
		
	}
	
	private void FindLights() {
		lights = GetTargetObject().GetComponentsInChildren<Light> (true);
		int lightCount = lights.Length;
		
		oldColors = new Color32[lightCount];
		oldIntensities = new float[lightCount];
		
		for (int i = 0; i < lightCount; i++) {
			oldColors[i] = lights[i].color;
			oldIntensities[i] = lights[i].intensity;
		}
	}
}
