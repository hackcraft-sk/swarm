
using UnityEngine;

public class SpriteOrderInLayerChangeEffect : GraphicalEffect{
	
	public int newValue = 0;
	public bool affectInactiveObjects = false;

	public override void PerformChange () {
		SpriteRenderer[] childrenRenderers = GetTargetObject().GetComponentsInChildren<SpriteRenderer> (affectInactiveObjects);
		
		foreach (SpriteRenderer renderer in childrenRenderers) {
			renderer.sortingOrder = newValue;
		}
	}
}
