using System;
using UnityEngine;

public class BannerPicker : MonoBehaviour
{
	public Texture[] banners;

	public Sprite targetSprite;

	int bannerIndex = -1;

	public void setNextBanner() {
		bannerIndex = bannerIndex + 1;
		bannerIndex %= banners.Length;

		Texture bannerTexture = banners [bannerIndex];
		targetSprite.texture = bannerTexture;
	}
}