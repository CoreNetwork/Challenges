package us.corenetwork.challenges;

public class PlayerRank {
	private String rank;
	private Integer neededPoints;
	private String group;
	private String suffix;

    public PlayerRank(String rank, Integer neededPoints, String group, String suffix) {
        this.rank = rank;
        this.neededPoints = neededPoints;
        this.group = group;
        this.suffix = suffix;
    }

    public String getRank() {
        return rank;
    }

    public Integer getNeededPoints() {
        return neededPoints;
    }

    public String getGroup() {
        return group;
    }

    public String getSuffix() {
        return suffix;
    }
}
