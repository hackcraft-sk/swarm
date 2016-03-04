
using UnityEngine;
using System.Collections;

public class GravityModifierEffect : ContinuousGraphicalEffect {
	
	public float newValue;

	public bool overrideStartValue = false;
	public float startValue;

	
	protected override void Prepare() {
		CheckTargetObjectHasRigidbody ();
		if (!overrideStartValue) {
			startValue = GetTargetObject().rigidbody2D.gravityScale;
		}
	}
	
	protected override void PerformContinuousChange() {
		float newVal = newValue*currentStatus + startValue*(1-currentStatus);
		GetTargetObject().rigidbody2D.gravityScale = newVal;
	}
	
	protected override void Cleanup() {
		
	}

	private void CheckTargetObjectHasRigidbody() {
		if (GetTargetObject ().GetComponent<Rigidbody2D> () == null) {
			Debug.LogWarning("Gravity modifier applied to object without rigidbody2D! " + gameObject);
		}
	}
}
