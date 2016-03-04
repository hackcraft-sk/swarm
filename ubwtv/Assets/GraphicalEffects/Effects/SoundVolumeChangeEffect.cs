
using System;
using UnityEngine;

public class SoundVolumeChangeEffect : ContinuousGraphicalEffect{

	public float targetVolume = 1f;

	public bool customStartingVolume = false;
	public float startingVolume;

	private Fabric.Component targetComponent;

	protected override void Prepare() {
		CheckAndSetTargetObject ();
		if (!customStartingVolume) {
			SetStartingVolume ();
		}
	}
	
	protected override void PerformContinuousChange() {
		targetComponent.Volume = (1 - currentStatus) * startingVolume + currentStatus * targetVolume;
	}
	
	protected override void Cleanup() {
		
	}
	
	private void CheckAndSetTargetObject() {
		targetComponent = GetTargetObject ().GetComponent<Fabric.Component> ();

		if (targetObject == null) {
			Debug.LogError ("Effect fed with wrong parameter, no music component in game object.");
		}
	}

	private void SetStartingVolume() {
		startingVolume = targetComponent.Volume;
	}
}
