using System;
using UnityEngine;
public class Interpolation
{
	public enum Type {linear, exponential, reverseExponential, smoothstep, smootherstep, 
					quarterCircle, reverseQuarterCircle};

	public static float Calculate(float value, Type interpolation) {

		switch (interpolation) {
			case (Type.linear): {
				return value;
			}
			case (Type.exponential): {
				return value*value;
			}
			case (Type.reverseExponential): {
				return 1 - ((1-value) * (1-value));
			}
			case (Type.smoothstep): {
				return Smoothstep(value);
			}
			case (Type.smootherstep): {
				return Smootherstep(value);
			}
			case (Type.quarterCircle) : {
				return 1 - Mathf.Sqrt(1 - value*value);
			}
			case (Type.reverseQuarterCircle) : {
				return Mathf.Sqrt(1 - (1-value)*(1-value));
			}
		}

		throw new ApplicationException("Interpolation class error - should never happen, sourcecode fucked.");
	}

	private static float Smoothstep(float value) {
		return value*value*(3 - 2*value);
	}

	private static float Smootherstep(float value) {
		return value*value*value*(value*(value*6 - 15) + 10);
	}
}

