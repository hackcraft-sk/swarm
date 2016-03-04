using System;
using UnityEngine;

public class ColliderActivationEffect : GraphicalEffect{
	public enum ActivationMode {activate, deactivate, swap};

	public ActivationMode mode = ActivationMode.swap;

	public override void PerformChange () {
		Collider2D[] childrenColliders = GetTargetObject().GetComponentsInChildren<Collider2D> (true);

		foreach (Collider2D collider in childrenColliders) {
			collider.enabled = GetNewActivationStatus(collider.enabled);
		}
	}

	private bool GetNewActivationStatus(bool currentStatus) {
		switch (mode) {
		case ActivationMode.activate:
			return true;
		case ActivationMode.deactivate:
			return false;
		case ActivationMode.swap:
			return !currentStatus;
		}
		throw new ApplicationException("Error in enum mode switch. Reached switch end without returning.");
	}
}
