
using UnityEngine;
using System.Collections;

public class ScaleEffect : ContinuousGraphicalEffect {

	public Vector3 newScaleVector;

	private Vector3 originalScale;
	
	protected override void Prepare() {
		originalScale = GetTargetObject ().transform.localScale;
	}
	
	protected override void PerformContinuousChange() {
		Vector3 nextScale = Vector3.Lerp (originalScale, newScaleVector, currentStatus);
		
		GetTargetObject().transform.localScale = nextScale;
	}
	
	protected override void Cleanup () {
	}
}