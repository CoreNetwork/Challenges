package us.corenetwork.challenges;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Util {

	private static Random random = new Random();
	
	public static void Message(String message, CommandSender sender)
	{
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
	
		for (int i = 0; i < lines.length; i++)
			sender.sendMessage(lines[i]);
	}
	
	public static void Broadcast(String message, String exclusion)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (!p.getName().equals(exclusion))
				Util.Message(message, p);
		}

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
