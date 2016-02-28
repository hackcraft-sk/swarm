package sk.hackcraft.bwtv.fancyOverlay;

import java.awt.*;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JFrame;

import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.bwtv.EventInfo;
import sk.hackcraft.bwtv.MatchEventListener;
import sk.hackcraft.bwtv.MatchInfo;
import sk.hackcraft.bwtv.Overlay;
import sk.hackcraft.bwtv.connections.WebConnection;
import sk.hackcraft.bwtv.swing.ScreenPainter;
import sk.hackcraft.bwtv.utils.BannerPicker;

public class JFrameOverlay extends JFrame implements Overlay
{
	private static final long serialVersionUID = 1L;

	private Map<MatchEvent, ScreenPainter> painters;
	private ScreenPainter activePainter;

	private final WebConnection webConnection;

	public JFrameOverlay(WebConnection webConnection)
	{
		super("StarCraft Overlay Info Panel");

		this.webConnection = webConnection;

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setAlwaysOnTop(true);

		setLocationRelativeTo(null);

		painters = new EnumMap<>(MatchEvent.class);
		activePainter = new ScreenPainter() {
			@Override
			public void setMatchInfo(MatchInfo matchInfo)
			{
			}

			@Override
			public void paint(Component component, Graphics2D g2d)
			{
			}
		};

		Container contentPane = getContentPane();

		Dimension size = new Dimension(640, 480);
		contentPane.setSize(size);
		contentPane.setMinimumSize(size);
		contentPane.setPreferredSize(size);

		setBackground(new Color(0, 255, 0));

		setResizable(false);

		BannerPicker bannerPicker = new BannerPicker();

		addStatePainter(MatchEvent.PREPARE, new MatchPrepareStatePainter());
		addStatePainter(MatchEvent.RUN, new MatchRunStatePainter());
		addStatePainter(MatchEvent.END, new MatchEndStatePainter());

		pack();
	}

	public void addStatePainter(MatchEvent state, ScreenPainter statePainter)
	{
		painters.put(state, statePainter);
	}

	public void setState(EventInfo stateInfo)
	{
		MatchEvent event = stateInfo.getEvent();
		activePainter = painters.get(event);

		int matchId = stateInfo.getMatchId();
		try {
			MatchInfo matchInfo = webConnection.getMatchInfo(matchId);
			activePainter.setMatchInfo(matchInfo);
			repaint();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void changeScreen(EventInfo eventInfo)
	{
		setState(eventInfo);
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		Dimension size = getSize();
		g2d.clearRect(0, 0, size.width, size.height);

		activePainter.paint(this, g2d);

		g2d.dispose();
		Toolkit.getDefaultToolkit().sync();
	}
}
