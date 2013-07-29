package com.matejdro.bukkit.mcnsa.challenges.usercommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.mcnsa.challenges.Setting;
import com.matejdro.bukkit.mcnsa.challenges.Settings;
import com.matejdro.bukkit.mcnsa.challenges.Util;

public abstract class BaseUserCommand {
	public Boolean needPlayer;
	public String desc;
	public String permission;

	public abstract Boolean run(CommandSender sender, String[] args);
	
	public Boolean execute(CommandSender sender, String[] args)
	{
		if (Settings.getBoolean(Setting.STOPPED) && !(this instanceof PointsCommand) && !(this instanceof UserHelpCommand))
		{
			Util.Message(Settings.getString(Setting.MESSAGE_STOPPED), sender);
			return true;
		}
		
		if (args.length > 0 && !Util.isInteger(args[0]))
		{
			String[] newargs = new String[args.length - 1];
			for (int i = 1; i < args.length; i++)
			{
				newargs[i - 1] = args[i];
			}
			args = newargs;			
		}
		
		if (!(sender instanceof Player) && needPlayer) 
		{
			Util.Message("Sorry, but you need to execute this command as player.", sender);
			return false;
		}
		
		if (!hasPermission(sender)) 
		{
			Util.Message(Settings.getString(Setting.MESSAGE_NO_PERMISSION), sender);
			return false;
		}
		
		return run(sender, args);
	}
	
	public boolean hasPermission(CommandSender sender)
	{
		 return Util.hasPermission(sender, "mcnsachallenges.player.".concat(permission));
	}

}