
using UnityEngine;

public class ObjectHierarchyChangeEffect : GraphicalEffect{
	
	public GameObject newParent;

	public override void PerformChange () {
		GetTargetObject ().transform.parent = (newParent != null)? newParent.transform : null;
	}
}
