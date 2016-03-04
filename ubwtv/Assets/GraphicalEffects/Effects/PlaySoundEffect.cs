
using UnityEngine;

public class PlaySoundEffect : GraphicalEffect{
	
	public override void PerformChange () {
		Fabric.EventListener l = GetTargetObject ().GetComponent<Fabric.EventListener> ();

		AudioManager.PlaySound(l._eventName);
	}
}
