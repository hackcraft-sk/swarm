package sk.hackcraft.als.utils;

public class MarkAchievement implements Achievement {

    private final String name;

    public MarkAchievement(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Achievement) {
            Achievement achievement = (Achievement) obj;
            return achievement.getName().equals(name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
