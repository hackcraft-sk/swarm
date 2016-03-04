
using UnityEngine;

public class MusicEndSynchronizerEffect : GraphicalEffect{

	public GraphicalEffect[] storedEffects;

	public override void PerformChange () {

		Debug.LogError ("Effect currently not working, do not use, please. " + gameObject.name);
		//CheckTargetObjectIsMusic ();
		//AddCallbackDelegateToTargetObject ();
	}

	/*private void CheckTargetObjectIsMusic() {
		if (GetTargetObject ().GetComponent<Fabric.Component>() == null) {
			Debug.LogError("Target object is not a fabric sound component. Cannot wait for it to end.");
		}
	}

	private void AddCallbackDelegateToTargetObject() {
		var musicComponent = GetTargetObject ().GetComponent<Fabric.Component> ();
		//musicComponent._onFinishPlaying += MusicEndCallback;
	}

	private void RemoveCallbackDelegateFromTargetObject() {
		var musicComponent = GetTargetObject ().GetComponent<Fabric.Component> ();
		//musicComponent._onFinishPlaying -= MusicEndCallback;
	}

	private void MusicEndCallback(double time) {
		foreach (GraphicalEffect effect in storedEffects) {
			PrepareAndScheduleEffect(effect, time);
		}

		RemoveCallbackDelegateFromTargetObject();

		if (repeatMode == RepeatMode.rescheduling) {
			EnqueueToScheduler();
		}
	}

	private void PrepareAndScheduleEffect(GraphicalEffect effect, double time) {
		float occurTime = effect.occurTimeDelay + (float) time;
		effect.EnqueueToScheduler (occurTime);
	}

	protected override void Reschedule() {
		
	}*/
}