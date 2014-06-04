package us.corenetwork.challenges.model;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.corenetwork.challenges.WeekUtil;

@DatabaseTable(tableName = "weekly_completed")
public class LevelSubmission {
    @DatabaseField(columnName = "Player")
    private String playerName;
    private Player player = null;

    @DatabaseField(columnName = "Level", foreign = true)
    private ChallengeLevel level = null;
    @DatabaseField(columnName = "ID", id = true, generatedId = true)
    private int ID;

    @DatabaseField(columnName = "State")
    private int stateNum;
    private ChallengeState state = null;

    @DatabaseField(columnName = "X")
    private int X;
    @DatabaseField(columnName = "Y")
    private int Y;
    @DatabaseField(columnName = "Z")
    private int Z;
    @DatabaseField(columnName = "World")
    private String World;
    private Location submittedLocation;

    @DatabaseField(columnName = "WGRegion")
    private String worldGuardRegion;
    @DatabaseField(columnName = "WGWorld")
    private String worldGuardWorld;

    @DatabaseField(columnName = "ClaimedBy")
    private String claimedByName;
    @DatabaseField(columnName = "moderator")
    private String moderatorName;
    private Player claimedBy, moderator;
    @DatabaseField(columnName = "lastUpdate")
    private long lastUpdate;

    LevelSubmission() {
        // for ormlite
    }

    public LevelSubmission(Player player, ChallengeLevel level, Location submittedLocation) {
        this.player = player;
        this.playerName = player.getName();
        this.level = level;

        setSubmittedLocation(submittedLocation);
        update();
    }

    public Player getPlayer() {
        if (player == null) {
            player = Bukkit.getPlayerExact(playerName);
        }
        return player;
    }

    public ChallengeLevel getLevel() {
        return level;
    }

    public int getID() {
        return ID;
    }

    public ChallengeState getState() {
        if (state == null) {
            state = ChallengeState.getByCode(stateNum);
        }
        return state;
    }

    public Location getSubmittedLocation() {
        if (submittedLocation == null) {
            submittedLocation = new Location(Bukkit.getWorld(World), X, Y, Z);
        }
        return submittedLocation;
    }

    public String getWorldGuardRegion() {
        return worldGuardRegion;
    }

    public String getWorldGuardWorld() {
        return worldGuardWorld;
    }

    public Player getClaimedBy() {
        if (claimedBy == null) {
            claimedBy = Bukkit.getPlayer(claimedByName);
        }
        return claimedBy;
    }

    public Player getModerator() {
        if (moderator == null) {
            moderator = Bukkit.getPlayer(moderatorName);
        }
        return moderator;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    private void update() {
        lastUpdate = WeekUtil.getCurrentTime();
    }

    public void setState(ChallengeState state) {
        this.state = state;
        this.stateNum = state.code();
        update();
    }

    public void setSubmittedLocation(Location submittedLocation) {
        this.submittedLocation = submittedLocation;
        X = submittedLocation.getBlockX();
        Y = submittedLocation.getBlockY();
        Z = submittedLocation.getBlockZ();
        World = submittedLocation.getWorld().getName();
        update();
    }

    public void setWorldGuardRegion(String worldGuardRegion) {
        this.worldGuardRegion = worldGuardRegion;
        update();
    }

    public void setWorldGuardWorld(String worldGuardWorld) {
        this.worldGuardWorld = worldGuardWorld;
        update();
    }

    public void setClaimedBy(Player claimedBy) {
        this.claimedBy = claimedBy;
        if (claimedBy != null) {
            claimedByName = claimedBy.getName();
        }
        update();
    }

    public void setModerator(Player moderator) {
        this.moderator = moderator;
        if (moderator != null) {
            moderatorName = moderator.getName();
        }
        update();
    }
}
