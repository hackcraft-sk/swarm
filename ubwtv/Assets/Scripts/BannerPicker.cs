using System;
using UnityEngine;
using UnityEngine.UI;

public class BannerPicker : MonoBehaviour
{
	public Texture[] banners;

	public RawImage bannerRenderer;

	int bannerIndex = -1;

	public void setNextBanner() {
		bannerIndex = bannerIndex + 1;
		bannerIndex %= banners.Length;

		Texture banner = banners [bannerIndex];
		bannerRenderer.texture = banner;
	}
}