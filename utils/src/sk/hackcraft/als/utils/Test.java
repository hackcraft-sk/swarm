package sk.hackcraft.als.utils;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import sk.hackcraft.als.utils.IniFileConfig.IniPair;
import sk.hackcraft.als.utils.IniFileConfig.IniSection;
import sk.hackcraft.als.utils.appcore.EventLoop;

public class Test
{
	public static void main(String[] args)
	{
		try
		{
			final EventLoop eventLoop = new EventLoop();
			
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					eventLoop.start();
				}
			}).start();
			
			eventLoop.post(new Runnable()
			{
				@Override
				public void run()
				{
					System.out.println("1");
				}
			});
			
			eventLoop.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					System.out.println("2");
				}
			}, 100000);
			
			eventLoop.post(new Runnable()
			{
				@Override
				public void run()
				{
					System.out.println("3");
				}
			});
			
			eventLoop.post(new Runnable()
			{
				@Override
				public void run()
				{
					System.out.println("4");
				}
			});
			
			eventLoop.post(new Runnable()
			{
				@Override
				public void run()
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							JOptionPane.showMessageDialog(null, "huh");
							
							eventLoop.post(new Runnable()
							{
								@Override
								public void run()
								{
									System.out.println("from swing");
								}
							});
						}
					});
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
