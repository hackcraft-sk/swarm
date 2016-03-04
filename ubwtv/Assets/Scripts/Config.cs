using UnityEngine;
using System;
using System.IO;
using System.Collections;

[Serializable]
public class Config {

	public string address;
	public int port;

	public string web;

	public bool mock;
	public int mockMatchId;

}
