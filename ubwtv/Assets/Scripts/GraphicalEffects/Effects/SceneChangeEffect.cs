
using UnityEngine;

public class SceneChangeEffect : GraphicalEffect{

	public string sceneName;

	public override void PerformChange() {
		Application.LoadLevel (sceneName);
	}
}


