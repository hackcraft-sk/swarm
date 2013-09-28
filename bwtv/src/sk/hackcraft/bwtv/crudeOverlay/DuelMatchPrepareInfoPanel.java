package sk.hackcraft.bwtv.crudeOverlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sk.hackcraft.bwtv.swing.JLabelBuilder;

public class DuelMatchPrepareInfoPanel extends JPanel
{
	private static final long serialVersionUID = -7088038277512660865L;

	public DuelMatchPrepareInfoPanel(String player1, String player2, String tournamentName)
	{
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel player1Label, vsLabel, player2Label, tournamentNameLabel;
		
		Font namesFont = new Font("sans", Font.PLAIN, 30);
		Font vsFont = new Font("sans", Font.PLAIN, 40);
		Font tournamentFont = new Font("sans", Font.PLAIN, 20);
		
		JLabelBuilder builder = new JLabelBuilder();
		builder
		.setForegroundColor(Color.WHITE)
		.setFont(namesFont)
		.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		player1Label = builder.create(player1);
		player2Label = builder.create(player2);
		
		builder
		.setFont(vsFont);
		vsLabel = builder.create("VS");
		
		builder
		.setFont(tournamentFont);
		tournamentNameLabel = builder.create(tournamentName);

		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		
		add(player1Label, c);
		
		c.gridx += 1;
		c.weightx = 0;
		
		add(vsLabel, c);
		
		c.gridx += 1;
		c.weightx = 1;
		
		add(player2Label, c);
		
		c.gridx = 0;
		c.gridy += 1;
		
		c.gridwidth = 3;
		
		add(tournamentNameLabel, c);
		
		setBackground(Color.BLACK);
	}
}
