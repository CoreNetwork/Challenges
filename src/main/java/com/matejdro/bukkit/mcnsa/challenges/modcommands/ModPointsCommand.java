package com.matejdro.bukkit.mcnsa.challenges.modcommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcnsa.challenges.PlayerPoints;
import com.matejdro.bukkit.mcnsa.challenges.Setting;
import com.matejdro.bukkit.mcnsa.challenges.Settings;
import com.matejdro.bukkit.mcnsa.challenges.Util;

public class ModPointsCommand extends BaseModCommand {
	
	public ModPointsCommand()
	{
		desc = "Manage player points";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {
		if (args.length < 1 || (args.length > 1 && !Util.isInteger(args[1])))
		{
			Util.Message("Usage: /chm points [Name] (Change) (Reason)", sender);
			return true;
		}
		
		if (args.length < 2)
			checkPoints(sender, args);
		else
			modifyPoints(sender, args);
		
		return true;
	}
	
	private void checkPoints(CommandSender sender, String[] args)
	{
		int points = PlayerPoints.getPoints(args[0]);
		
		String message = Settings.getString(Setting.MESSAGE_PLAYER_POINTS);
		message = message.replace("<Player>", args[0]);
		message = message.replace("<Points>", Integer.toString(points));
		
		Util.Message(message, sender);
	}
	
	private void modifyPoints(CommandSender sender, String[] args)
	{
		int curPoints = PlayerPoints.getPoints(args[0]);
		int change = Integer.parseInt(args[1]);
		
		String message = Settings.getString(Setting.MESSAGE_PLAYER_POINTS_ALTERED);
		message = message.replace("<Player>", args[0]);
		message = message.replace("<Change>", Integer.toString(change));
		Util.Message(message, sender);
		
		String reason = null;
		if (args.length > 2)
		{
			reason = "";
			for (int i = 2; i < args.length; i++)
				reason += args[i] + " ";
			
			reason = reason.trim();
		}
		PlayerPoints.addPoints(args[0], change, reason);

	}

}
