
using UnityEngine;

public class MusicChangeEffect : GraphicalEffect{
	
	
	public override void PerformChange () {
		TargetObjectCheck ();
		MusicController musicController = GameObject.FindObjectOfType<MusicController> ();
		musicController.PlayNext (GetTargetObject ());
	}

	private void TargetObjectCheck() {
		if (GetTargetObject ().GetComponent<Fabric.Component> () == null) {
			Debug.LogError("Music change effect fed with wrong parameter, no music component in game object.");
		}
	}
}