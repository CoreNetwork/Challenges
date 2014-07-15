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


public class DenyCommand extends BaseModCommand {
	
	public DenyCommand()
	{
		desc = "Delete and reject challenge submission";
		needPlayer = false;
		permission = "deny";
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = Challenges.lastTeleport.get(((Player) sender).getName());
			if (id == null)
			{
				Util.Message("Usage: /chm deny [ID] (Message)", sender);
				return true;
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_USING_PREVIOUS_ID).replace("<ID>", Integer.toString(id)), sender);
			}
			
		}
		else
			id = Integer.parseInt(args[0]);
		
		String message = null;
		if (args.length > 1 || (args.length > 0 && !Util.isInteger(args[0])))
		{
			message = "";
			for (int i = Util.isInteger(args[0]) ? 1 : 0; i < args.length; i++)
				message += args[i] + " ";
			
			message = message.trim();
		}

        UUID playerUUID = null;
		String level = "";
		int weekId = 0;
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT State, Player, Level, WeekID FROM weekly_completed WHERE ID = ?");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				ChallengeState state = ChallengeState.getByCode(set.getInt("State"));
				level = Integer.toString(set.getInt("Level"));
				weekId = set.getInt("WeekID");
				if (state != ChallengeState.SUBMITTED)
				{
					Util.Message(Settings.getString(Setting.MESSAGE_DELETE_ONLY_OPEN_CHALLENGES), sender);
					set.close();
					statement.close();
					return true;
				}
				
				playerUUID = Util.getUUIDFromString(set.getString("Player"));
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_INVALID_ID), sender);
				set.close();
				statement.close();
				return true;
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		Player player = Bukkit.getServer().getPlayer(playerUUID);
		if (player != null)
		{
			if (message == null)
				Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_REJECTED).replace("<Level>", level), player);
			else
				Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_REJECTED_MESSAGE).replace("<Message>", message).replace("<Level>", level), player);
		}
		
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET state = 2, ModResponse=?, lastUpdate=?, moderator=? WHERE state <> 1 AND WeekID = ? AND Player = ?");
			statement.setString(1, message == null ? "" : message);
			statement.setInt(2, (int) (System.currentTimeMillis() / 1000));
			statement.setString(3, ((Player) sender).getUniqueId().toString());
			statement.setInt(4, weekId);
			statement.setString(5, playerUUID.toString());
			statement.executeUpdate();
			statement.close();
			
			IO.getConnection().commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		WorldEditHandler.clearSelection((Player) sender);
		Util.Message(Settings.getString(Setting.MESSAGE_DELETED), sender);
		return true;
	}

}
