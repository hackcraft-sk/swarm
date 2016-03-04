
using UnityEngine;

public class EffectRandomPick : GraphicalEffect {
	
	public bool allowImmediateRepeat = true;
	public GraphicalEffect[] storedEffects;

	private GraphicalEffect lastEffect = null;

	public override void PerformChange () {
		CheckAtLeastOneEffect ();

		GraphicalEffect next = PickRandomEffect ();
		if (!allowImmediateRepeat && CheckAtLeastTwoEffects()) {

			while (next == lastEffect) {
				next = PickRandomEffect();
			}
		}

		lastEffect = next;
		next.EnqueueToScheduler ();
	}
	
	private void PrepareAndScheduleEffect(GraphicalEffect effect) {

		if (rescheduling && effect.rescheduling) {
			effect.rescheduling = false;
		}
		if (runAtStart) {
			effect.runAtStart = false;
		}
		
		effect.EnqueueToScheduler();
	}

	private GraphicalEffect PickRandomEffect() {
		int index = Random.Range (0, storedEffects.Length);
		return storedEffects [index];
	}

	private void CheckAtLeastOneEffect() {
		if (storedEffects.Length == 0) {
			Debug.LogWarning("Random pick effect has no underlying effects. Exception will be thrown when running.");
		}
	}

	private bool CheckAtLeastTwoEffects() {
		if (storedEffects.Length <= 1) {
			Debug.LogWarning("Random pick effect settings incorrect. Cannot non-repeatedly play a single effect");
			return false;
		}
		return true;
	}
}
