using UnityEngine;
using System.Collections;

public class RunScreenController : ScreenController {

	protected override void OnActivation() {
        gameObject.SetActive(true);
    }

	protected override void OnDeactivation() {
	}

}
