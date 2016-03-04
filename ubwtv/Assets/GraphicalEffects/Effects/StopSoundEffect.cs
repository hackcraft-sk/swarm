

using UnityEngine;

public class StopSoundEffect : GraphicalEffect{

	public string soundEffectName;
	
	public override void PerformChange () {
		MusicController musicController = GameObject.FindObjectOfType<MusicController> ();
		musicController.stopMusic ();
	}
}