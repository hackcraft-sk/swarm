using UnityEngine;
using System.Collections;

public class RotationEffect : ContinuousGraphicalEffect {

	public bool relative = true;
	public float rotationValue;

	private float rotateBy;

	private float startRotation;
	private float endRotation;

	protected override void Prepare() {
		CalculateRotations ();
	}
	
	protected override void PerformContinuousChange() {

		GetTargetObject ().transform.Rotate (new Vector3 (0, 0, rotateBy * currentStatusDelta));
	}

	protected override void Cleanup () {

	}

	private void CalculateRotations() {
		if (relative) {
			rotateBy = rotationValue;
		} else {
			rotateBy = rotationValue - GetTargetObject().transform.eulerAngles.z;
		}
	}
}