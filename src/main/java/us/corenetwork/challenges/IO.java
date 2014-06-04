package us.corenetwork.challenges;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import us.corenetwork.challenges.model.ChallengeLevel;
import us.corenetwork.challenges.model.LevelSubmission;

public class IO {
	private static ConnectionSource connection;
    private static Dao<LevelSubmission, Integer> levelSubmissionDao;
    private static Dao<ChallengeLevel, Integer> challengeLevelDao

	public static synchronized ConnectionSource getConnection() {
		if (connection == null) {
            connection = createConnection();
        }
		return connection;
	}

	public static ArrayList<PlayerRank> ranks = new ArrayList<PlayerRank>();

	private static ConnectionSource createConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
            ConnectionSource source = new JdbcConnectionSource("jdbc:sqlite:" +  new File(Challenges.instance.getDataFolder().getPath(), "data.sqlite").getPath());
            try {
                levelSubmissionDao = DaoManager.createDao(source, LevelSubmission.class);
                challengeLevelDao = DaoManager.createDao(source, ChallengeLevel.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return source;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

    public static Dao<ChallengeLevel, Integer> getChallengeLevelDao() {
        return challengeLevelDao;
    }

    public static Dao<LevelSubmission, Integer> getLevelSubmissionDao() {
        return levelSubmissionDao;
    }

    public static synchronized void freeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
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

			loadRanks();

			saveConfig();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			//
			e.printStackTrace();
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
            ConfigurationSection section = (ConfigurationSection) e.getValue();
			PlayerRank rank = new PlayerRank(e.getKey(), section.getInt("PointsNeeded"), section.getString("Group"), section.getString("Suffix"));
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
		ConnectionSource conn;
		Statement st = null;
		try {
			conn = IO.getConnection();//            {
			st = conn.;
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
        update("SELECT LevelID FROM weekly_completed LIMIT 1", "ALTER TABLE weekly_completed ADD COLUMN LevelID int;" +
                "UPDATE weekly_completed SET LevelID = (SELECT ID FROM weekly_levels WHERE weekly_levels.WeekID = weekly_completed.WeekID AND weekly_levels.Level = weekly_completed.Level);");
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
				Challenges.log.log(Level.SEVERE, "[Challenges] Error while updating tables to the new version - " + e.getMessage());
				e.printStackTrace();
			}

		}

	}
}
