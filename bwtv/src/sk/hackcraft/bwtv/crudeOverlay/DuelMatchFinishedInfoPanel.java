package sk.hackcraft.bwtv.crudeOverlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sk.hackcraft.bwtv.swing.JLabelBuilder;

public class DuelMatchFinishedInfoPanel extends JPanel
{
	private static final long serialVersionUID = 9036778311846058758L;

	public DuelMatchFinishedInfoPanel(String resultText, String pointsText)
	{
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(Color.BLACK);
		
		Font resultFont = new Font("sans", Font.PLAIN, 30);
		Font pointsFont = new Font("sans", Font.PLAIN, 20);
		
		JLabelBuilder builder = new JLabelBuilder();
		builder
		.setForegroundColor(Color.WHITE)
		.setFont(resultFont)
		.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JLabel resultLabel = builder.create(resultText);
		
		builder.setFont(pointsFont);
		
		JLabel pointsLabel = builder.create(pointsText);
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		
		add(resultLabel, c);
		
		c.gridy += 1;
		
		add(pointsLabel, c);
	}
}
