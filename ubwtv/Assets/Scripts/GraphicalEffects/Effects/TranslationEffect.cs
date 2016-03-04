using UnityEngine;
using System.Collections;

public class TranslationEffect : ContinuousGraphicalEffect {
	public enum CoordSystemType {relative, local, world};

	public CoordSystemType coordSystem = CoordSystemType.relative;
	public Vector3 movementVector;

	private Vector3 targetPosition;
	private Vector3 originalPosition;
	
	protected override void Prepare() {
		CalculateTargetPosition ();
	}
	
	protected override void PerformContinuousChange() {
		Vector3 nextPosition = currentStatus * targetPosition + (1 - currentStatus) * originalPosition;

		GetTargetObject().transform.position = nextPosition;
	}
	
	protected override void Cleanup () {

	}

	private void CalculateTargetPosition() {
		originalPosition = GetTargetObject().transform.position;
		switch (coordSystem) {

			case CoordSystemType.relative:
				targetPosition = originalPosition + movementVector;
				break;

			case CoordSystemType.local:
				targetPosition = transform.parent.TransformPoint(movementVector);
				break;

			case CoordSystemType.world:
				targetPosition = movementVector;
				break;
		}
	}
}