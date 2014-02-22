package sk.hackcraft.bwtv.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

public class BannerPicker
{
	private Queue<BufferedImage> banners;

	public BannerPicker()
	{
		banners = new LinkedList<>();
	}

	public void addBanner(String name) throws IOException
	{
		banners.add(ImageIO.read(new File("banners/" + name + ".bmp")));
	}

	public BufferedImage pickBanner()
	{
		BufferedImage banner = banners.remove();
		banners.add(banner);

		return banner;
	}
}
