using System;
using UnityEngine;

public class BannerPicker : MonoBehaviour
{
	public Sprite[] banners;

	public SpriteRenderer bannerRenderer;

	int bannerIndex = -1;

	public void setNextBanner() {
		bannerIndex = bannerIndex + 1;
		bannerIndex %= banners.Length;

		Sprite banner = banners [bannerIndex];
		bannerRenderer.sprite = banner;
	}
}