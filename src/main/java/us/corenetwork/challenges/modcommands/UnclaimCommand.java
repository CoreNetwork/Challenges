package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Challenges;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class UnclaimCommand extends BaseModCommand {
	
	public UnclaimCommand()
	{
		desc = "Remove moderator claim from submission";
		needPlayer = true;
		permission = "unclaim";
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = Challenges.lastTeleport.get(((Player) sender).getName());
			if (id == null)
			{
				Util.Message("Usage: /chm unclaim [ID]", sender);
				return true;
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_USING_PREVIOUS_ID).replace("<ID>", Integer.toString(id)), sender);
			}
			
		}
		else
			id = Integer.parseInt(args[0]);
		
		unclaimSubmission(id);
		Util.Message(Settings.getString(Setting.MESSAGE_MOD_UNCLAIM_ID).replaceAll("<ID>", Integer.toString(id)), sender);
		
		return true;
	}
	
	public static void unclaimPlayer(UUID player)
	{
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET ClaimedBy = NULL WHERE ClaimedBy = ?");
			statement.setString(1, player.toString());
			
			statement.executeUpdate();
			IO.getConnection().commit();
			statement.close();

			Player player1 = Bukkit.getPlayer(player);
			if (player1 != null)
			{
				Util.Message(Settings.getString(Setting.MESSAGE_MOD_UNCLAIM), player1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void unclaimSubmission(int id)
	{
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET ClaimedBy = NULL WHERE ID = ?");
			statement.setInt(1, id);
			
			statement.executeUpdate();
			IO.getConnection().commit();
			statement.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
