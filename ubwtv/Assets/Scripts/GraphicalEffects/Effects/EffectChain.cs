using UnityEngine;

public class EffectChain : GraphicalEffect{
	
	public GraphicalEffect[] effects;
	public bool autoCalcRescheduleTime = false;
	
	public override void PerformChange () {
		CheckAtLeastOneEffect ();

		float effectStartTime = 0f;
		foreach (GraphicalEffect effect in effects) {
			effect.EnqueueToScheduler(effect.occurTimeDelay + effectStartTime);

			effectStartTime += effect.occurTimeDelay;
			if (effect is ContinuousGraphicalEffect) {
				effectStartTime += ((ContinuousGraphicalEffect)effect).duration;
			}
		}

		if (autoCalcRescheduleTime && rescheduling) {
			rescheduleTimeDelay = effectStartTime + originalRescheduleTimeDelay;
		}
	}
	
	private void CheckAtLeastOneEffect() {
		if (effects.Length == 0) {
			Debug.LogWarning("Effect Chain has no underlying effects. Nothing will happen when running.");
		}
	}
}