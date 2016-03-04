using UnityEngine;
using System.Collections.Generic;
using System.Linq;

public class EffectScheduler : MonoBehaviour {
	
	private SortedList<float, GraphicalEffect> events = new SortedList<float, GraphicalEffect> ();
	private GraphicalEffect firstElement;
	private float firstElementOccurTime = float.MaxValue;

	void Update () {
		while (Time.time >= firstElementOccurTime) {
			events.Remove(firstElementOccurTime);

			firstElement.Activate();

			UpdateFirst();
		}
	}

	public void Schedule(GraphicalEffect effect, float waitTime){
		float occurTime = Time.time + waitTime;
		if (events.ContainsKey(occurTime)){
			GraphicalEffect found = events[occurTime];
			found.AddAnotherEffect(effect);
		} else {
			events.Add (occurTime, effect);
		}
		UpdateFirst (effect, occurTime);
	}

	public bool Unschedule(GraphicalEffect effect) {
		bool found = false;
		foreach (KeyValuePair<float, GraphicalEffect> kvp in events)
		{
			if (kvp.Value == effect) {
				events.Remove(kvp.Key);
				found = true;
				if (effect.HasNextEffect()) {
					events.Add(kvp.Key, effect.GetNextEffect());
				}
			} else if (SearchAndRemoveFromLinkedList(kvp.Value, effect)){
				found = true;
			}
		}
		return found;
	}

	private void UpdateFirst() {

		if (events.Count == 0) {
			firstElement = null;
			firstElementOccurTime = float.MaxValue;
		} else {
			var first = events.First();
			firstElement = first.Value;
			firstElementOccurTime = first.Key;
		}
	}

	private void UpdateFirst(GraphicalEffect newElement, float occurTime) {
		if (occurTime < firstElementOccurTime) {
			firstElement = newElement;
			firstElementOccurTime = occurTime;
		}
	}

	private bool SearchAndRemoveFromLinkedList(GraphicalEffect listHead, GraphicalEffect removed) {
		while (listHead.HasNextEffect()) {
			if (listHead.GetNextEffect() == removed) {
				listHead.RemoveNextEffect();
				return true;
			} else {
				listHead = listHead.GetNextEffect();
			}
		}
		return false;
	}
}
