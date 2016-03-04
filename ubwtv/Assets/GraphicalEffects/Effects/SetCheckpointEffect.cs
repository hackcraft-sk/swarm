using UnityEngine;

public class SetCheckpointEffect : GraphicalEffect{

	public override void PerformChange () {
		GameManager.instance.setCheckpoint(GetNewCheckpoint ());
	}

	private Vector3 GetNewCheckpoint() {

		return GetTargetObject ().transform.position;
	}
}