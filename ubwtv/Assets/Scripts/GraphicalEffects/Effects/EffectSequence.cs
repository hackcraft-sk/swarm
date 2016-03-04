using UnityEngine;

public class EffectSequence : GraphicalEffect{
	
	public GraphicalEffect[] effects;
	private int currentEffect = 0;

	public override void PerformChange () {
		CheckAtLeastOneEffect ();
		effects[currentEffect].EnqueueToScheduler();
		IncrementCurrentEffectPointer();
	}

	protected override void IncrementInvocationCount() {
		if (currentEffect == 0) {
			invocationCount++;
		}
	}

	private void IncrementCurrentEffectPointer() {
		currentEffect = (currentEffect + 1) % effects.Length;
	}

	private void CheckAtLeastOneEffect() {
		if (effects.Length == 0) {
			Debug.LogWarning("Sequential effect has no underlying effects. Exception will be thrown when running.");
		}
	}
}