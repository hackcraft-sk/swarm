using System;
using UnityEngine;
using UnityEngine.UI;

public class BannerPicker : MonoBehaviour
{
	public Banner[] banners;

	public Text bannerText;
	public RawImage bannerImage;
	public Text bannerUrlText;

	int bannerIndex = -1;

	public void setNextBanner() {
		bannerIndex = bannerIndex + 1;
		bannerIndex %= banners.Length;

		Banner banner = banners [bannerIndex];
		bannerText.text = banner.text;
		bannerImage.texture = banner.texture;
		if (bannerUrlText != null) {
			bannerUrlText.text = banner.url;
		}
	}
}