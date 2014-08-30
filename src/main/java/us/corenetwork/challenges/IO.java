package us.corenetwork.challenges;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.evilmidget38.UUIDFetcher;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
public class IO {
	private static Connection connection;

	public static synchronized Connection getConnection() {
		if (connection == null) connection = createConnection();
		return connection;
	}

	public static ArrayList<PlayerRank> ranks = new ArrayList<PlayerRank>();

	private static Connection createConnection() {
		try {
            File oldFile = new File(Challenges.instance.getDataFolder(), "data.sqlite");
            File newFile = new File(Challenges.instance.getDataFolder(), "challenges.sqlite");
            if (oldFile.exists() && !newFile.exists()) {
                oldFile.renameTo(newFile);
            }
            Class.forName("org.sqlite.JDBC");
			Connection ret = DriverManager.getConnection("jdbc:sqlite:" +  newFile.getPath());
			ret.setAutoCommit(false);
			return ret;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized void freeConnection() {
		Connection conn = getConnection();
		if(conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void LoadSettings()
	{
		try {
			SettingType.CONFIG.setConfig(new YamlConfiguration());
			SettingType.STORAGE.setConfig(new YamlConfiguration());
			SettingType.CONFIG.setConfigFile(new File(Challenges.instance.getDataFolder(), "config.yml"));
			SettingType.STORAGE.setConfigFile(new File(Challenges.instance.getDataFolder(), "storage.yml"));

			if (!SettingType.STORAGE.getConfigFile().exists())
			{
				SettingType.STORAGE.save();
			}
			if (!SettingType.CONFIG.getConfigFile().exists())
			{
				SettingType.CONFIG.save();
			}

			SettingType.CONFIG.load();
			SettingType.STORAGE.load();
			for (Setting s : Setting.values())
			{
				YamlConfiguration config = s.getSettingType().getConfig();
				if (config.get(s.getString()) == null && s.getDefault() != null) config.set(s.getString(), s.getDefault());
			}

			if (SettingType.CONFIG.getConfig().get(Setting.PLAYER_CLASSES.getString()) == null)
				setDefaultClasses();

			if (SettingType.STORAGE.getConfig().get(Setting.CURRENT_WEEK_START.getString()) == null)
				SettingType.STORAGE.getConfig().set(Setting.CURRENT_WEEK_START.getString(), WeekUtil.getCurrentWeekCalculatedStart());



            Integer oldVersion = Settings.getInt(Setting.STORAGE_VERSION);
            Integer newVersion = 1;
            if (oldVersion < newVersion) {
                upgradeStorage(oldVersion, newVersion);
            }

            SettingType.STORAGE.getConfig().set(Setting.STORAGE_VERSION.getString(), newVersion);

            loadRanks();
            saveConfig();

			File userCache = new File(Challenges.instance.getDataFolder(), "usercache.yml");
			if (userCache.exists())
			{
				Util.loadUserCache(userCache);
			}

			Util.loadUsersYml(new File(Settings.getString(Setting.GROUPMANAGER_USERS_FILE)));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			//
			e.printStackTrace();
		}
	}

    private static Set<String> getAllPlayerNames() {
        String queries[] = {
                "SELECT PLAYER AS p FROM player_points",
                "SELECT Player AS p FROM point_changes",
                "SELECT Player AS p FROM weekly_completed",
                "SELECT moderator AS p FROM weekly_completed"
        };
        try {

            Set<String> players = new HashSet<String>();
            for (String query : queries) {
                PreparedStatement stm = IO.getConnection().prepareStatement(query);
                stm.execute();
                ResultSet set = stm.getResultSet();
                while (set.next()) {
                    String p = set.getString("p");
                    if (p == null) {
                        continue;
                    }
                    players.add(p);
                }
            }
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_SET;
    }


    private static void upgradeStorage(int oldVersion, int newVersion) {
        if (oldVersion == 0) { // player names instead of UUID in database
            try {
                Challenges.log.info("Upgrading player names to UUIDs. This might take a while. Under no circumstances terminate the process, since this will hurt your database");
                Challenges.log.info("Searching for player names ...");
                // cache player-to-uuid so the whole process is faster
                Set<String> playerNames = getAllPlayerNames();
                Challenges.log.info("UUID lookup for " + playerNames.size() + " names ...");
                Util.lookupUUIDs(playerNames);
                // upgrade table player_points
                Challenges.log.info("Upgrading table 1/3 ...");
                PreparedStatement stm = IO.getConnection().prepareStatement("SELECT PLAYER FROM player_points");
                stm.execute();
                ResultSet results = stm.getResultSet();
                PreparedStatement update = IO.getConnection().prepareStatement("UPDATE player_points SET PLAYER=? WHERE PLAYER=?");
                while (results.next()) {
                    String playerName = results.getString("PLAYER");
                    UUID uuid = Util.getPlayerUUIDFromName(playerName);
                    update.setString(1, uuid.toString());
                    update.setString(2, playerName);
                    update.addBatch();
                }
                update.executeBatch();
                update.close();

                // upgrade table point_changes
                Challenges.log.info("Upgrading table 2/3 ...");
                stm = IO.getConnection().prepareStatement("SELECT ID, Player FROM point_changes");
                stm.execute();
                results = stm.getResultSet();
                update = IO.getConnection().prepareStatement("UPDATE point_changes SET Player=? WHERE ID=?");
                while (results.next()) {
                    String playerName = results.getString("Player");
                    int ID = results.getInt("ID");
                    UUID uuid = Util.getPlayerUUIDFromName(playerName);
                    update.setString(1, uuid.toString());
                    update.setInt(2, ID);
                    update.addBatch();
                }
                update.executeBatch();
                update.close();

                // upgrade table weekly_completed
                Challenges.log.info("Upgrading table 3/3 ...");
                stm = IO.getConnection().prepareStatement("SELECT ID, Player, moderator FROM weekly_completed");
                stm.execute();
                results = stm.getResultSet();
                update = IO.getConnection().prepareStatement("UPDATE weekly_completed SET Player=?, moderator=? WHERE ID=?");
                while (results.next()) {
                    String playerName = results.getString("Player");
                    String moderatorName = results.getString("moderator");
                    UUID player = Util.getPlayerUUIDFromName(playerName);
                    UUID moderator = moderatorName != null ? Util.getPlayerUUIDFromName(moderatorName) : null;
                    int ID = results.getInt("ID");
                    update.setString(1, player.toString());
                    update.setString(2, moderator != null ? moderator.toString() : null);
                    update.setInt(3, ID);
                    update.addBatch();
                }
                update.executeBatch();
                update.close();
                Challenges.log.info("Upgrade complete");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig()
	{
		try {
			SettingType.CONFIG.save();
			SettingType.STORAGE.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadRanks()
	{
		for (Entry<String, Object> e : SettingType.CONFIG.getConfig().getConfigurationSection(Setting.PLAYER_CLASSES.getString()).getValues(false).entrySet())
		{
			PlayerRank rank = new PlayerRank();
			rank.rank = e.getKey();

			ConfigurationSection section = (ConfigurationSection) e.getValue();
			rank.neededPoints = section.getInt("PointsNeeded");
			rank.group = section.getString("Group");
			rank.suffix = section.getString("Suffix");

			ranks.add(rank);
		}
	}

	private static void setDefaultClasses()
	{
		String cPrefix = Setting.PLAYER_CLASSES.getString() + ".";
		int[] classesPoints = new int[]{0,1,3,7,12,18,25,33,42,52,63,75,88,102,117,133,150,168,187,207,228,250,273,297,322,348,375,403,432,462};
		String[] classes = new String[]{"Novice", "Flatcorian", "Nomad", "Raider", "Mercenary", "Merchant", "Scribe", "Savant", "Special"};
		String[] prefixes = new String[]{"", "Adept ", "Veteran ", "Master "};
		String[] suffixes = new String[]{"", "+", "++", "+++"};
		int counter = 0;
		for (String pClass : classes)
		{
			for (int pId = 0; pId < prefixes.length; pId++)
			{
				String prefix = prefixes[pId];

				if ((pClass.equals("Novice") || pClass.equals("Special")) && !prefix.equals(""))
					continue;

				int points = classesPoints[counter];

				YamlConfiguration config = SettingType.CONFIG.getConfig();
				config.set(cPrefix + prefix + pClass + ".PointsNeeded", points);
				config.set(cPrefix + prefix + pClass + ".Group", pClass);
				config.set(cPrefix + prefix + pClass + ".Suffix", suffixes[pId]);
				counter++;
			}
		}
	}

	public static void PrepareDB()
	{
		Connection conn;
		Statement st = null;
		try {
			conn = IO.getConnection();//            {
			st = conn.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS weekly_levels (ID INTEGER PRIMARY KEY NOT NULL, WeekID INTEGER NOT NULL, Level Integer, Description STRING, Points INTEGER)");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS weekly_completed(ID INTEGER PRIMARY KEY NOT NULL, WeekID INTEGER, Level INTEGER, Player STRING, State INTEGER, X INTEGER, Y INTEGER, Z INTEGER, World STRING, WGRegion STRING, WGWorld, ModResponse STRING, ClaimedBy STRING, moderator STRING)");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS player_points (PLAYER STRING, POINTS INTEGER)");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS point_changes (ID INTEGER PRIMARY KEY, Player String, Amount Integer, Reason String)");
			conn.commit();
			st.close();
		} catch (SQLException e) {
			Challenges.log.log(Level.SEVERE, "[Challenges]: Error while creating tables! - " + e.getMessage());
			e.printStackTrace();
		}
		UpdateDB();
	}

	public static void UpdateDB()
	{
		update("SELECT lastUpdate FROM weekly_completed LIMIT 1", "ALTER TABLE weekly_completed ADD lastUpdate INTEGER");
		update("SELECT WGWorld FROM weekly_completed LIMIT 1", "ALTER TABLE weekly_completed ADD WGWorld STRING");
		update("SELECT moderator FROM weekly_completed LIMIT 1", "ALTER TABLE weekly_completed ADD moderator STRING");
	}

	public static void update(String check, String sql)
	{
		try
		{
			Statement statement = getConnection().createStatement();
			statement.executeQuery(check);
			statement.close();
		}
		catch(SQLException ex)
		{
			Challenges.log.log(Level.INFO, "[Challenges] Updating database");
			try {
				String[] query;
				query = sql.split(";");
				Connection conn = getConnection();
				Statement st = conn.createStatement();
				for (String q : query)
					st.executeUpdate(q);
				conn.commit();
				st.close();
			} catch (SQLException e) {
				Challenges.log.log(Level.SEVERE, "[Jail] Error while updating tables to the new version - " + e.getMessage());
				e.printStackTrace();
			}

		}

	}
}
