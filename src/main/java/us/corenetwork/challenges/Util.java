package us.corenetwork.challenges;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {
	
	public static void Message(String message, CommandSender sender)
	{
		message = message.replaceAll("\\&([0-9abcdef])", ChatColor.COLOR_CHAR + "$1");
		
		String color = "f";
		final int maxLength = 59; //Max length of chat text message
        final String newLine = "[NEWLINE]";
        ArrayList<String> chat = new ArrayList<String>();
        chat.add(0, "");
        String[] words = message.split(" ");
        int lineNumber = 0;
        for (int i = 0; i < words.length; i++) {
                if (chat.get(lineNumber).replaceAll("\\" + ChatColor.COLOR_CHAR + "([0-9abcdef])", "").length() + words[i].replaceAll("\\" + ChatColor.COLOR_CHAR + "([0-9abcdef])", "").length() < maxLength && !words[i].equals(newLine)) {
                        chat.set(lineNumber, chat.get(lineNumber) + (chat.get(lineNumber).length() > 0 ? " " : ChatColor.COLOR_CHAR + color ) + words[i]);

                        if (words[i].contains(String.valueOf(ChatColor.COLOR_CHAR))) color = Character.toString(words[i].charAt(words[i].lastIndexOf(String.valueOf(ChatColor.COLOR_CHAR)) + 1));
                }
                else {
                        lineNumber++;
                        if (!words[i].equals(newLine)) {
                                chat.add(lineNumber,  ChatColor.COLOR_CHAR + color + words[i]);
                        }
                        else
                                chat.add(lineNumber, "");
                }
        }
        for (int i = 0; i < chat.size(); i++) {
        	{
    			sender.sendMessage(chat.get(i));
        	}
        	
        }
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
}
