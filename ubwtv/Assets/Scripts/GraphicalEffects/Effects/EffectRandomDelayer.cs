
using UnityEngine;

public class EffectRandomDelayer : GraphicalEffect{
	
	public float minimumDelay;
	public float maximumDelay;
	public Interpolation.Type distributionMappingCurve = Interpolation.Type.linear;

	public bool autoCalcRescheduleTime = false;

	private GraphicalEffect effect;
	
	public override void PerformChange () {
		CheckTargetObjectHasEffect ();

		float delay = CalculateRandomValue ();

		PrepareAndScheduleEffect (effect, delay);

		if (autoCalcRescheduleTime) {
			rescheduleTimeDelay = delay + originalRescheduleTimeDelay;
		}
	}

	private float CalculateRandomValue() {
		float delayDiff = maximumDelay - minimumDelay;
		float randomVal = Random.value;
		float interpolated = Interpolation.Calculate (randomVal, distributionMappingCurve); 
		float delay = minimumDelay + (interpolated * delayDiff);

		return delay;
	}

	private void PrepareAndScheduleEffect(GraphicalEffect effect, float delay) {

		if (rescheduling && effect.rescheduling) {
			effect.rescheduling = false;
		}
		if (runAtStart) {
			effect.runAtStart = false;
		}
		
		effect.EnqueueToScheduler(effect.occurTimeDelay + delay);
	}
	
	private void CheckTargetObjectHasEffect() {
		if (targetObject != null || targetObject.GetComponent<GraphicalEffect> () != null) {

			effect = targetObject.GetComponent<GraphicalEffect> ();
		} else {

			Debug.LogWarning("Target object is not a Graphical Effect. Cannot run it.");
		}
	}
}