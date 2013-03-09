package com.matejdro.bukkit.flatcoreweekly;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class Settings {
	
	public static Object getProperty(Setting setting)
	{
		Object property = IO.config.get(setting.getString());
		if (property == null)
		{
			FlatcoreWeekly.log.warning("[FlatcoreWeekly] Configuration entry missing: " + setting.getString());
			property = setting.getDefault();
		}
		
		return property;
	}

	public static Boolean getBoolean(Setting setting)
	{
		return 	(Boolean) getProperty(setting);
	}
	
	public static Integer getInt(Setting setting)
	{
		return 	(Integer) getProperty(setting);
	}

	public static String getString(Setting setting)
	{
		return 	(String) getProperty(setting);
	}
	
	public static String getCommandDescription(String cmd, String type)
	{
		String path = "CommandDescriptions." + type + "." + cmd;
		
		Object descO = IO.config.get(path);
		if (descO == null)
		{
			IO.config.set(path, "&a/" + type + " " + cmd + " &8-&f " + "<INSERT DESCRIPTION HERE>");
			try {
				IO.config.save(new File(FlatcoreWeekly.instance.getDataFolder(),"config.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			descO = IO.config.get(path);
		}
		
		return (String) descO;
		
	}
	
	public static List<?> getList(Setting setting)
	{
		return 	(List<?>) getProperty(setting);
	}
}
