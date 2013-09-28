package sk.hackcraft.bwtv.control;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import sk.hackcraft.bwtv.Overlay;
import sk.hackcraft.bwtv.stream.NullStream;
import sk.hackcraft.bwtv.stream.Stream;
import sk.hackcraft.bwtv.stream.StreamFactory;

public class Remote
{
	private final StreamFactory streamFactory;
	private final Overlay overlay;
	
	private Stream stream;

	private JFrame remoteFrame;
	private JFrame captureAreaFrame;
	
	private JButton moveUp, moveDown, moveLeft, moveRight;
	private JToggleButton toggleStream, toggleOverlay;
	private JLabel labelState;
	
	public Remote(StreamFactory streamFactory, Overlay overlay)
	{
		this.streamFactory = streamFactory;
		this.overlay = overlay;
		
		stream = new NullStream();
		
		remoteFrame = new JFrame("Remote");
		
		remoteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		remoteFrame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (isStreamRunning())
				{
					int result = JOptionPane.showConfirmDialog(remoteFrame, "Stream is running, stop it and close?");
					
					if (result != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
				
				disposeAll();
			}
		});
		
		remoteFrame.setResizable(false);
		
		Container contentPane = remoteFrame.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		
		moveUp = new JButton("↑");
		moveUp.addActionListener(new MoveActionListener(0, -1));
		
		moveDown = new JButton("↓");
		moveDown.addActionListener(new MoveActionListener(0, 1));
		
		moveLeft = new JButton("←");
		moveLeft.addActionListener(new MoveActionListener(-1, 0));
		
		moveRight = new JButton("→");
		moveRight.addActionListener(new MoveActionListener(1, 0));
		
		toggleStream = new JToggleButton("Stream");
		toggleOverlay = new JToggleButton("Overlay");
		
		labelState = new JLabel();
		labelState.setHorizontalAlignment(JLabel.CENTER);
		setLabelState(false);
		
		toggleStream.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (toggleStream.isSelected())
				{
					startStream();
				}
				else
				{
					stopStream();
				}
			}
		});
		
		toggleOverlay.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (toggleOverlay.isSelected())
				{
					showOverlay();
				}
				else
				{
					hideOverlay();
				}
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 1;
		c.gridy = 0;
		contentPane.add(moveUp, c);

		c.gridx -= 1;
		c.gridy += 1;
		contentPane.add(moveLeft, c);
		
		c.gridx += 2;
		contentPane.add(moveRight, c);

		c.gridx -= 1;
		c.gridy += 1;
		contentPane.add(moveDown, c);

		c.gridx -= 1;
		c.gridy += 1;
		c.gridwidth = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(toggleStream, c);
		
		c.gridy += 1;
		contentPane.add(toggleOverlay, c);
		
		c.gridy += 1;
		contentPane.add(labelState, c);
		
		remoteFrame.pack();
		
		captureAreaFrame = new JFrame("BWTV Stream Capture Area");
		
		captureAreaFrame.setResizable(false);
		captureAreaFrame.setUndecorated(true);
		captureAreaFrame.setFocusable(false);
		
		captureAreaFrame.setSize(new Dimension(640, 480));
		captureAreaFrame.getContentPane().setBackground(Color.RED);
		captureAreaFrame.setLocation(100, 100);
		captureAreaFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		captureAreaFrame.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentMoved(ComponentEvent e)
			{
				captureAreaMoved();
			}
		});
		
		captureAreaFrame.setVisible(true);
		remoteFrame.setVisible(true);
	}
	
	private void disposeAll()
	{
		if (!stream.isStopped())
		{
			stopStream();
		}
		
		remoteFrame.dispose();
		captureAreaFrame.dispose();
		
		overlay.dispose();
	}
	
	private void startStream()
	{
		try
		{
			Point location = captureAreaFrame.getLocation();
			stream = streamFactory.create((int)location.getX(), (int)location.getY());

			setLabelState(true);
			captureAreaFrame.setVisible(false);
			
			setMovementButtons(false);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(remoteFrame, e.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
			
			toggleStream.setSelected(false);
		}
	}
	
	private void stopStream()
	{
		stream.stop();
		
		setLabelState(false);
		captureAreaFrame.setVisible(true);
		
		setMovementButtons(true);
	}
	
	private void setMovementButtons(boolean enabled)
	{
		moveUp.setEnabled(enabled);
		moveLeft.setEnabled(enabled);
		moveRight.setEnabled(enabled);
		moveDown.setEnabled(enabled);
	}
	
	private void moveCaptureArea(int offsetX, int offsetY)
	{
		Point location = captureAreaFrame.getLocationOnScreen();
		
		Point newLocation = new Point((int)location.getX() + offsetX, (int)location.getY() + offsetY);

		captureAreaFrame.setLocation(newLocation);
		overlay.setLocation((int)newLocation.getX(), (int)newLocation.getY());
	}
	
	private void captureAreaMoved()
	{
		if (isStreamRunning())
		{
			stopStream();
		}
		
		Point captureAreaLocation = captureAreaFrame.getLocation();
		overlay.setLocation((int)captureAreaLocation.getX(), (int)captureAreaLocation.getY());
	}
	
	private void showOverlay()
	{
		Point captureAreaLocation = captureAreaFrame.getLocation();
		
		int x = (int)captureAreaLocation.getX();
		int y = (int)captureAreaLocation.getY();
		
		overlay.setLocation(x, y);
		overlay.setVisible(true);
	}
	
	private void hideOverlay()
	{
		overlay.setVisible(false);
	}
	
	private void setLabelState(boolean streaming)
	{
		if (streaming)
		{
			labelState.setText("On Air");
			labelState.setForeground(Color.RED);
		}
		else
		{
			labelState.setText("Off Air");
			labelState.setForeground(Color.BLACK);
		}
	}
	
	private boolean isStreamRunning()
	{
		return stream != null && !stream.isStopped();
	}
	
	private class MoveActionListener implements ActionListener
	{
		private final int moveX, moveY;
		
		public MoveActionListener(int offsetX, int offsetY)
		{
			this.moveX = offsetX;
			this.moveY = offsetY;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{			
			int offsetX = moveX;
			int offsetY = moveY;
		
			int modifiers = e.getModifiers();
			boolean fastMove = (modifiers & InputEvent.CTRL_MASK) != 0;
			
			if (fastMove)
			{
				offsetX *= 10;
				offsetY *= 10;
			}
			
			moveCaptureArea(offsetX, offsetY);
		}
	}
}
