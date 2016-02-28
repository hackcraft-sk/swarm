package sk.hackcraft.bwtv.fancyOverlay;

import sk.hackcraft.bwtv.MatchInfo;
import sk.hackcraft.bwtv.swing.ScreenPainterBase;

import java.awt.*;

public class MatchEndStatePainter extends ScreenPainterBase {
    private MatchInfo matchInfo;

    @Override
    public void setMatchInfo(MatchInfo matchInfo) {
        this.matchInfo = matchInfo;
    }

    @Override
    public void paint(Component component, Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(100, 170, 460, 120);

        g2d.setColor(Color.WHITE);

        String matchResult = "WIN";
        String winner = "bot1";
        String looser = "bot2";

        switch (matchResult) {
            case "WIN":
                paintMatchResult(g2d, winner + " wins!");
                paintPoints(g2d, winner + " +3pts");
                break;
            case "PARTIAL_WIN":
                paintMatchResult(g2d, winner + " partially wins!");
                paintPoints(g2d, winner + " +2pts, " + looser + " +1pts");
                break;
            case "DRAW":
                paintMatchResult(g2d, "Draw!");
                paintPoints(g2d, "+1pts to both");
                break;
            case "INVALID":
                paintMatchResult(g2d, "Error!");
                paintPoints(g2d, "Match will be replayed later.");
                break;
            default:
                throw new IllegalArgumentException("Wrong match result: " + matchResult);
        }
    }

    private void paintMatchResult(Graphics2D g2d, String text) {
        paintCenteredText(g2d, text, 220, 30);
    }

    private void paintPoints(Graphics2D g2d, String text) {
        paintCenteredText(g2d, text, 250, 20);
    }

}
