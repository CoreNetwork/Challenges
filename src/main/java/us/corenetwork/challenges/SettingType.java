package us.corenetwork.challenges;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public enum SettingType {
	CONFIG, STORAGE;

	private File configFile;
	private YamlConfiguration config;

	public YamlConfiguration getConfig()
	{
		return config;
	}

	public void setConfig(YamlConfiguration config)
	{
		this.config = config;
	}

	public File getConfigFile()
	{
		return configFile;
	}

	public void setConfigFile(File configFile)
	{
		this.configFile = configFile;
	}

	public void save() throws IOException
	{
		config.save(configFile);
	}

	public void load() throws IOException, InvalidConfigurationException
	{
		config.load(configFile);
	}
}
