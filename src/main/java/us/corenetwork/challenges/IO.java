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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
public class IO {
    private static Connection connection;
    public static YamlConfiguration config;
        
    public static synchronized Connection getConnection() {
    	if (connection == null) connection = createConnection();
    	return connection;
    }
    
    public static ArrayList<PlayerRank> ranks = new ArrayList<PlayerRank>();

    private static Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection ret = DriverManager.getConnection("jdbc:sqlite:" +  new File(Challenges.instance.getDataFolder().getPath(), "data.sqlite").getPath());
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
    		config = new YamlConfiguration();

    		if (!new File(Challenges.instance.getDataFolder(),"config.yml").exists()) config.save(new File(Challenges.instance.getDataFolder(),"config.yml"));

    		config.load(new File(Challenges.instance.getDataFolder(),"config.yml"));
	    	for (Setting s : Setting.values())
	    	{
	    		if (config.get(s.getString()) == null && s.getDefault() != null) config.set(s.getString(), s.getDefault());
	    	}
	    	
	    	if (config.get(Setting.PLAYER_CLASSES.getString()) == null)
	    		setDefaultClasses();
	    	
	    	if (config.get(Setting.CURRENT_WEEK_START.getString()) == null)
	    		config.set(Setting.CURRENT_WEEK_START.getString(), WeekUtil.getCurrentWeekCalculatedStart());
	    	
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
			config.save(new File(Challenges.instance.getDataFolder(),"config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void loadRanks()
    {
    	for (Entry<String, Object> e : config.getConfigurationSection(Setting.PLAYER_CLASSES.getString()).getValues(false).entrySet())
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
            st.executeUpdate("CREATE TABLE IF NOT EXISTS weekly_completed(ID INTEGER PRIMARY KEY NOT NULL, WeekID INTEGER, Level INTEGER, Player STRING, State INTEGER, X INTEGER, Y INTEGER, Z INTEGER, World STRING, WGRegion STRING, WGWorld, ModResponse STRING, ClaimedBy STRING)");
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
