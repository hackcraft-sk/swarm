using UnityEngine;
using System.Collections;

public class BlockColorChangeEffect : ContinuousGraphicalEffect {
	
	public Color32 color;

	public bool customStartColor = false;
	public Color32 startColor;

	public bool affectInactiveObjects = true;

	private SpriteRenderer[] sprites;
	
	protected override void Prepare() {
		LoadSprites ();
		if (!customStartColor) {
			SetStartColor ();
		}
	}

	protected override void PerformContinuousChange() {
		Color32 newColor = Color32.Lerp (startColor, color, currentStatus);

		foreach (SpriteRenderer sprite in sprites) {
			sprite.color = newColor;
		}
	}
	
	protected override void Cleanup() {

	}

	private void LoadSprites() {
		sprites = GetTargetObject ().GetComponentsInChildren<SpriteRenderer> (affectInactiveObjects);
	}
	
	private void SetStartColor() {
		if (sprites.Length > 0) {
			startColor = sprites[0].color;
		}
	}
}
