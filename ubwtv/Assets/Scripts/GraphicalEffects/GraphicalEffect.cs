using UnityEngine;
using System;
using System.Collections;

public abstract class GraphicalEffect : MonoBehaviour {

	public bool runAtStart;
	public bool rescheduling = false;
	public float rescheduleTimeDelay;
	public float occurTimeDelay;

	public GameObject targetObject;
	public int maximumRepeats = 0;

	protected float originalRescheduleTimeDelay;
	protected int invocationCount = 0;

	private GraphicalEffect nextElement;

	public void Start() {
		originalRescheduleTimeDelay = rescheduleTimeDelay;
		if (runAtStart) {
			EnqueueToScheduler();
		}
	}

	public void Activate() {
		if (isActiveAndEnabled) {
			PerformChange ();
			IncrementInvocationCount ();
			Reschedule ();
		}
		ActivateNext ();
	}

	public abstract void PerformChange();

	public void EnqueueToScheduler() {
		EnqueueToScheduler (occurTimeDelay);
	}

	public void EnqueueToScheduler(float customDelay) {
		
		if (maxRepeatsReached()) {
			return;
		} else {
			FindEffectScheduler ().Schedule (this, customDelay);
		}
	}

	public bool Unschedule() {
		return FindEffectScheduler ().Unschedule (this);
	}

	public void AddAnotherEffect(GraphicalEffect next) {
		AnotherEffectDeadlockCheck (next);
		if (nextElement != null) {
			nextElement.AddAnotherEffect(next);
		} else {
			nextElement = next;
		}
	}

	public void RemoveNextEffect() {
		if (nextElement != null) {
			nextElement = nextElement.nextElement;
		}
	}

	public bool HasNextEffect() {
		return nextElement != null;
	}

	public GraphicalEffect GetNextEffect() {
		return nextElement;
	}

	public GameObject GetTargetObject() {
		return (targetObject == null) ? gameObject : targetObject;
	}

	protected virtual void IncrementInvocationCount() {
		invocationCount++;
	}

	private void ActivateNext(){
		if (nextElement != null) {
			var next = nextElement;
			nextElement = null;
			next.Activate ();
		}
	}

	private EffectScheduler FindEffectScheduler() {
		return GameObject.FindObjectOfType<EffectScheduler> ();
	}

	protected virtual void Reschedule(){

		if (rescheduling && rescheduleTimeDelay != 0) {
			EnqueueToScheduler (rescheduleTimeDelay);
		}
	}

	private void AnotherEffectDeadlockCheck(GraphicalEffect anotherEffect) {
		if (this == anotherEffect) {
			throw new ApplicationException("Critical error in EffectScheduler, call Epholl. " + this + ", " + gameObject + " " + gameObject.name);
		}
	}

	private bool maxRepeatsReached() {
		return (maximumRepeats != 0 && invocationCount >= maximumRepeats );
	}
}
