using UnityEngine;

public class ObjectActivationEffect : GraphicalEffect{
	public enum ActivationMode {activate, deactivate, swap};

	public ActivationMode mode = ActivationMode.swap;

	public override void PerformChange () {
		switch (mode) {
		case ActivationMode.swap:
			GetTargetObject().SetActive (!GetTargetObject().activeSelf);
			break;

		case ActivationMode.activate:
			GetTargetObject().SetActive (true);
			break;

		case ActivationMode.deactivate:
			GetTargetObject().SetActive (false);
			break;
		}
	}
}