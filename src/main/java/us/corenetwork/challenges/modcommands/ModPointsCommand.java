package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import us.corenetwork.challenges.*;
import us.corenetwork.challenges.usercommands.PointsCommand;


public class ModPointsCommand extends BaseModCommand {
	
	public ModPointsCommand()
	{
		desc = "Manage player points";
		needPlayer = false;
		permission = "points";
	}


	public Boolean run(CommandSender sender, String[] args) {
		if (args.length < 1 || (args.length > 1 && !Util.isInteger(args[1])))
		{
			Util.Message("Usage: /chm points [Name] (Change) (Reason)", sender);
			return true;
		}
		
		if (args.length < 2)
			PointsCommand.printPoints(sender, Util.getPlayerUUIDFromName(args[0]));
		else
			modifyPoints(sender, args);
		
		return true;
	}
	
	private void modifyPoints(CommandSender sender, String[] args)
	{
		int change = Integer.parseInt(args[1]);
		
		Message message = Message.from(Setting.MESSAGE_PLAYER_POINTS_ALTERED);
        String playerName = args[0];
        OfflinePlayer player = Bukkit.getPlayer(Util.getPlayerUUIDFromName(playerName));
        message = message.variable("Player", player.getName());
		message = message.variable("Change", change);
		message.send(sender);
		
		String reason = null;
		if (args.length > 2)
		{
			reason = "";
			for (int i = 2; i < args.length; i++)
				reason += args[i] + " ";
			
			reason = reason.trim();
		}
		PlayerPoints.addPoints(player.getUniqueId(), change, reason);
	}

}
