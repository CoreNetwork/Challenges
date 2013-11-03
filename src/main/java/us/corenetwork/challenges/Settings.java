package us.corenetwork.challenges;

import java.util.List;


public class Settings {
	
	public static Object getProperty(Setting setting)
	{
		Object property = IO.config.get(setting.getString());
		if (property == null)
		{
			MCNSAChallenges.log.warning("[FlatcoreWeekly] Configuration entry missing: " + setting.getString());
			property = setting.getDefault();
		}
		
		return property;
	}

	public static Boolean getBoolean(Setting setting)
	{
		return 	(Boolean) getProperty(setting);
	}
	
	public static Long getLong(Setting setting)
	{
		return 	((Number) getProperty(setting)).longValue();
	}
	
	public static Integer getInt(Setting setting)
	{
		return 	((Number) getProperty(setting)).intValue();
	}

	public static String getString(Setting setting)
	{
		return 	(String) getProperty(setting);
	}
	
	public static String getCommandDescription(String cmd, String type, String def)
	{
		String path = "CommandDescriptions." + type + "." + cmd;
		
		Object descO = IO.config.get(path);
		if (descO == null)
		{
			IO.config.set(path, "&a/" + type + " " + cmd + " &8-&f " + def);
			IO.saveConfig();
			descO = IO.config.get(path);
		}
		
		return (String) descO;
		
	}
	
	public static List<?> getList(Setting setting)
	{
		return 	(List<?>) getProperty(setting);
	}
}
