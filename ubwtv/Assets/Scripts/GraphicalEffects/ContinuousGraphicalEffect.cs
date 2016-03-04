using UnityEngine;
using System.Collections;

public abstract class ContinuousGraphicalEffect : GraphicalEffect {

	public float duration;
	public Interpolation.Type interpolation = Interpolation.Type.linear;

	protected float currentStatus;
	protected float currentStatusDelta;
	private float statusDeltaSum;
	private float endTime;

	private bool coroutineRunning = false;

	public override void PerformChange() {
		Prepare ();
		CoroutineActivation ();
		Cleanup ();
	}

	public IEnumerator Coroutine() {

		coroutineRunning = true;
		while (Time.time <= endTime) {
			UpdateStatus();
			PerformContinuousChange();
			yield return null;
		}
		coroutineRunning = false;

		CoroutineCleanup ();
	}

	public void OnDisable() {
		coroutineRunning = false;
	}

	protected abstract void Prepare();

	protected abstract void PerformContinuousChange ();

	protected abstract void Cleanup();

	private void CoroutineActivation() {
		CoroutinePrepare ();

		if (!coroutineRunning) {
			StartCoroutine ("Coroutine");
		}
	}

	private void CoroutinePrepare() {
		endTime = Time.time + duration;
		ResetStatus ();
	}

	private void CoroutineCleanup() {
		CalculateImprecision ();
		PerformContinuousChange ();
	}

	private void UpdateStatus() {
		if (duration == 0) {
			currentStatus = currentStatusDelta = statusDeltaSum = 1f;
		} else {
			float newStatus = 1 - ((endTime - Time.time) / (duration));
			newStatus = Mathf.Abs(newStatus);
			float newStatusInterpolated = Interpolation.Calculate(newStatus, interpolation);

			currentStatusDelta = newStatusInterpolated - currentStatus;
			currentStatus = newStatusInterpolated;
			statusDeltaSum += currentStatusDelta;
		}
	}

	private void CalculateImprecision() {
		currentStatus = 1;
		currentStatusDelta = 1 - statusDeltaSum;
	}

	private void ResetStatus() {
		currentStatus = 0;
		currentStatusDelta = 0;
		statusDeltaSum = 0;
	}
}

