package sk.hackcraft.als.master.model;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.StandardAchievements;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AchievementModifier {

    public void modifyAchievements(MatchReport matchReport) {
        List<SlaveMatchReport> slaveMatchReports = matchReport.getSlavesMatchReports();
        boolean hasWinners = checkIfThereIsWinner(slaveMatchReports);
        if (!hasWinners) {
            changeResultsToDraw(slaveMatchReports);
        }
    }

    private boolean checkIfThereIsWinner(Collection<SlaveMatchReport> slaveMatchReports) {
        for (SlaveMatchReport slaveReport : slaveMatchReports) {
            Set<Achievement> achievements = slaveReport.getAchievements();

            if (achievements.contains(StandardAchievements.VICTORY)) {
                return true;
            }
        }

        return false;
    }

    private void changeResultsToDraw(Collection<SlaveMatchReport> slaveMatchReports) {
        for (SlaveMatchReport slaveReport : slaveMatchReports) {
            Set<Achievement> achievements = slaveReport.getAchievements();
            achievements.remove(StandardAchievements.DEFEAT);
            achievements.add(StandardAchievements.DRAW);
        }
    }
}
