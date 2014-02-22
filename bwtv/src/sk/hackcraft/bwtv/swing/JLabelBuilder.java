package sk.hackcraft.bwtv.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.border.Border;

public class JLabelBuilder
{
	private Color foregroundColor;
	private Font font;
	private Border border;

	public JLabelBuilder setForegroundColor(Color foregroundColor)
	{
		this.foregroundColor = foregroundColor;

		return this;
	}

	public JLabelBuilder setFont(Font font)
	{
		this.font = font;

		return this;
	}

	public JLabelBuilder setBorder(Border border)
	{
		this.border = border;

		return this;
	}

	public JLabel create(String text)
	{
		JLabel label = new JLabel(text);

		label.setForeground(foregroundColor);
		label.setFont(font);
		label.setBorder(border);

		return label;
	}
}
