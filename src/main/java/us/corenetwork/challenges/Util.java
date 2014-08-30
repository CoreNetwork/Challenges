package us.corenetwork.challenges;
import java.io.*;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.evilmidget38.NameFetcher;
import com.evilmidget38.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

public class Util {

	private static Random random = new Random();
    private static Map<String, UUID> playerNameToUUID = new HashMap<String, UUID>();
    private static Map<UUID, String> UUIDToPlayerName = new HashMap<UUID, String>();

    public static void Message(String message, CommandSender sender)
	{
        String[] lines = getMessageLines(message);

        for (String line : lines) {
            sender.sendMessage(line);
        }
	}

    public static String[] getMessageLines(String message) {
        message = message.replaceAll("\\&([0-9abcdefklmnor])", ChatColor.COLOR_CHAR + "$1");

        final String newLine = "\\[NEWLINE\\]";
        String[] lines = message.split(newLine);

        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();

            if (i == 0)
                continue;

            int lastColorChar = lines[i - 1].lastIndexOf(ChatColor.COLOR_CHAR);
            if (lastColorChar == -1 || lastColorChar >= lines[i - 1].length() - 1)
                continue;

            char lastColor = lines[i - 1].charAt(lastColorChar + 1);
            lines[i] = Character.toString(ChatColor.COLOR_CHAR).concat(Character.toString(lastColor)).concat(lines[i]);
        }
        return lines;
    }

    public static void Broadcast(String message, String exclusion)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (!p.getName().equals(exclusion))
				Util.Message(message, p);
		}
        Util.Message(message, Bukkit.getConsoleSender());

	}
		
	public static void MessagePermissions(String message, String permission)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (Util.hasPermission(p, permission))
				Util.Message(message, p);
		}
	}
	
    
    public static Boolean isInteger(String text) {
    	  try {
    	    Integer.parseInt(text);
    	    return true;
    	  } catch (NumberFormatException e) {
    	    return false;
    	  }
    	}
    
    public static boolean hasPermission(CommandSender player, String permission)
    {
    	while (true)
    	{
    		if (player.hasPermission(permission))
    			return true;
    		
    		if (permission.length() < 2)
    			return false;
    		
    		if (permission.endsWith("*"))
    			permission = permission.substring(0, permission.length() - 2);
    		
    		int lastIndex = permission.lastIndexOf(".");
    		if (lastIndex < 0)
    			return false;
    		
    		permission = permission.substring(0, lastIndex).concat(".*");  
    	}
    }

    public static void lookupUUIDs(Set<String> players) {
        UUIDFetcher fetcher = new UUIDFetcher(new ArrayList<String>(players));
        try {
            Map<String, UUID> res = fetcher.call();
            playerNameToUUID.putAll(res);
            for (Map.Entry<String, UUID> e : res.entrySet()) {
                UUIDToPlayerName.put(e.getValue(), e.getKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPlayerNameFromUUID(final UUID uuid) {
        if (uuid == null) {
            return "<no player>";
        }
        String name = UUIDToPlayerName.get(uuid);
        if (name == null) {
            name = Bukkit.getOfflinePlayer(uuid).getName();
        }
        if (name == null) {
            NameFetcher fetcher = new NameFetcher(new ArrayList<UUID>(){{add(uuid);}});
            try {
                Map<UUID, String> names = fetcher.call();
                name = names.get(uuid);
                if (name != null) {
                    UUIDToPlayerName.put(uuid, name);
                    playerNameToUUID.put(name, uuid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name != null ? name : "<" + uuid.toString() + ">";
    }

	public static void loadUsersYml(File file) {
		try {
			Yaml yaml = new Yaml();
			Map<String, Object> data = yaml.loadAs(new FileInputStream(file), Map.class);
			Map<String, Map> users = (Map<String, Map>) data.get("users");
			for (Map.Entry<String, Map> e : users.entrySet())
			{
				UUID uuid = null;
				try
				{
					uuid = getUUIDFromString(e.getKey());
				}
				catch (Exception e1)
				{
					continue;
				}
				Map<String, Object> playerData = e.getValue();
				String name = (String) playerData.get("lastname");

				UUIDToPlayerName.put(uuid, name);
				playerNameToUUID.put(name, uuid);
			}
		} catch (IOException e) {
			Challenges.instance.getLogger().warning("Couldn't find the users.yml file. Name from UUID lookup will be a bit slow when starting your server.");
		}
	}

	public static void saveUserCache(File file)
	{
		Yaml yaml = new Yaml();
		try
		{
			Map<String, String> dump = new HashMap<String, String>();
			for (Map.Entry<UUID, String> e : UUIDToPlayerName.entrySet())
			{
				dump.put(e.getKey().toString(), e.getValue());
			}
			yaml.dump(dump, new FileWriter(file));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void loadUserCache(File file)
	{
		Yaml yaml = new Yaml();
		try
		{
			Map<String, String> data = yaml.loadAs(new FileInputStream(file), Map.class);
			for (Map.Entry<String, String> e : data.entrySet())
			{
				UUID uuid = getUUIDFromString(e.getKey());
				UUIDToPlayerName.put(uuid, e.getValue());
				playerNameToUUID.put(e.getValue(), uuid);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}


	}

    @SuppressWarnings("deprecated")
    public static UUID getPlayerUUIDFromName(String name) {
        UUID id = playerNameToUUID.get(name);
        if (id != null) {
            return id;
        } else {
        }
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public static UUID getUUIDFromString(String string) {
        if (string == null) {
            return null;
        }
        return UUID.fromString(string);
    }

	public static void replaceAllHandlers(Logger logger, Handler replace)
	{
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		logger.addHandler(replace);
	}

	public static <T>T getRandomItem(T[] array)
	{
		return array[random.nextInt(array.length)];
	}

	public static <T>T getRandomItem(ArrayList<T> array)
	{
		return array.get(random.nextInt(array.size()));
	}

	public static class PluginLoggerHandler extends Handler {
		private Plugin plugin;
		private String prefix;

		public PluginLoggerHandler(Plugin plugin)
		{
			this.plugin = plugin;
			prefix = "[" + this.plugin.getDescription().getName() + "] ";
		}

		@Override
		public void publish(LogRecord record)
		{
			if (!record.getMessage().startsWith(prefix)) {
				record.setMessage(prefix + record.getMessage());
			}
			Bukkit.getLogger().log(record);
		}

		@Override
		public void flush()
		{
			// nothing
		}

		@Override
		public void close() throws SecurityException
		{
			// nothing
		}
	}
}
