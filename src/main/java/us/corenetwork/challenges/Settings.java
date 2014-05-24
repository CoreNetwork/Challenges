package us.corenetwork.challenges;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;


public class Settings {
	
	public static Object getProperty(Setting setting)
	{
		YamlConfiguration config = setting.getSettingType().getConfig();
		Object property = config.get(setting.getString());
		if (property == null)
		{
			Challenges.log.warning("[Challenges] Configuration entry missing: " + setting.getString());
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

		YamlConfiguration config = SettingType.CONFIG.getConfig();
		Object descO = config.get(path);
		if (descO == null)
		{
			config.set(path, "&a/" + type + " " + cmd + " &8-&f " + def);
			IO.saveConfig();
			descO = config.get(path);
		}
		
		return (String) descO;
		
	}
	
	public static List<?> getList(Setting setting)
	{
		return 	(List<?>) getProperty(setting);
	}
}
