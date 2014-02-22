package sk.hackcraft.bwtv.crudeOverlay;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sk.hackcraft.bwtv.swing.JLabelBuilder;

public class ErrorInfoPanel extends JPanel
{
	private static final long serialVersionUID = -1805536185411879678L;

	public ErrorInfoPanel()
	{
		setBackground(Color.BLACK);

		JLabelBuilder builder = new JLabelBuilder();
		builder.setFont(new Font("sans", Font.PLAIN, 30)).setForegroundColor(Color.WHITE).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel errorMessage = builder.create("Sorry, an error has occured.");

		add(errorMessage);
	}
}
