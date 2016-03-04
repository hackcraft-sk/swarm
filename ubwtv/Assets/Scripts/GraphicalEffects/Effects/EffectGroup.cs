using UnityEngine;

public class EffectGroup : GraphicalEffect{
	public enum ChildTargetsMode { noChange, setWhenEmpty, setEverywhere};
	public enum EffectsSearchMode { ownArray, childObjects, both};

	public ChildTargetsMode childTargetObjectsMode = ChildTargetsMode.setWhenEmpty;
	public EffectsSearchMode effectsStoredIn = EffectsSearchMode.childObjects;
	public GraphicalEffect[] storedEffects;

	public override void PerformChange () {
		GraphicalEffect[] childEffects = GetComponentsInChildren<GraphicalEffect> ();

		if (effectsStoredIn != EffectsSearchMode.ownArray) {
			foreach (GraphicalEffect effect in childEffects) {
				PrepareAndScheduleEffect(effect);
			}
		}
		if (effectsStoredIn != EffectsSearchMode.childObjects) {
			foreach (GraphicalEffect effect in storedEffects) {
				PrepareAndScheduleEffect(effect);
			}
		}

	}

	private void PrepareAndScheduleEffect(GraphicalEffect effect) {
		if (effect == this) {
			return;
		}

		if (rescheduling && effect.rescheduling) {
			effect.rescheduling = false;
		}
		if (runAtStart) {
			effect.runAtStart = false;
		}
		SetEffectTargetObject(effect);
		
		effect.EnqueueToScheduler();
	}

	private void SetEffectTargetObject(GraphicalEffect effect) {
		switch (childTargetObjectsMode) {

			case ChildTargetsMode.noChange: {
				break;
			}
			case ChildTargetsMode.setWhenEmpty: {
				if (effect.targetObject == null) {
					effect.targetObject = GetTargetObject ();
				}
				break;
			}
			case ChildTargetsMode.setEverywhere: {
				effect.targetObject = GetTargetObject ();
				break;
			}
		}
	}
}
