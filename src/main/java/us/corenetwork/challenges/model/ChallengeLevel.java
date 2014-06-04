package us.corenetwork.challenges.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "weekly_levels")
public class ChallengeLevel {
    @DatabaseField(columnName = "ID", id = true, generatedId = true)
    private int ID;
    @DatabaseField(columnName = "WeekID")
    private int weekID;
    @DatabaseField(columnName = "Level")
    private int level;
    @DatabaseField(columnName = "Description")
    private String description;
    @DatabaseField(columnName = "Points")
    private int points;

    ChallengeLevel() {
        // for ormlite
    }

    public ChallengeLevel(int weekID, int level, String description, int points) {
        this.weekID = weekID;
        this.level = level;
        this.description = description;
        this.points = points;
    }

    public int getID() {
        return ID;
    }

    public int getWeekID() {
        return weekID;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public int getPoints() {
        return points;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
