package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.*;


@Deprecated
public class ExplodeCommand extends BaseModCommand {
	
	public ExplodeCommand()
	{
		desc = "View all levels of specific submission";
		needPlayer = true;
		permission = "explode";
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = Challenges.lastTeleport.get(((Player) sender).getName());
			if (id == null)
			{
				Util.Message("Usage: /chm explode [ID]", sender);
				return true;
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_USING_PREVIOUS_ID).replace("<ID>", Integer.toString(id)), sender);
			}
			
		}
		else
			id = Integer.parseInt(args[0]);
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Player,WeekId FROM weekly_completed WHERE ID = ? LIMIT 1");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				String player = set.getString("Player");
				int week = set.getInt("WeekId");
				
				PreparedStatement newStatement = IO.getConnection().prepareStatement("SELECT Level, ID FROM weekly_completed WHERE Player = ? AND WeekID = ?");
				newStatement.setString(1, player);
				newStatement.setInt(2, week);
				
				ResultSet newSet = newStatement.executeQuery();
				while (newSet.next())
				{
					String message = Settings.getString(Setting.MESSAGE_EXPLODE_ENTRY);
					message = message.replace("<Level>", Integer.toString(newSet.getInt("Level")));
					message = message.replace("<ID>", Integer.toString(newSet.getInt("Id")));
					
					Util.Message(message, sender);
				}
				
				newStatement.close();
			
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_INVALID_ID), sender);
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
		return true;
	}
	
	private String getPlayerDataString(UUID player, int week) throws SQLException
	{
		String playerData = "";
		List<Integer> notSubmittedLevels = new ArrayList<Integer>(5);
		List<Integer> waitingLevels = new ArrayList<Integer>(5);
		List<Integer> approvedLevels = new ArrayList<Integer>(5);
		List<Integer> rejectedLevels = new ArrayList<Integer>(5);
		
		int levels;
		
		PreparedStatement statement = IO.getConnection().prepareStatement("SELECT COUNT(*) FROM weekly_levels WHERE weekID = ?");
		statement.setInt(1, week);
		ResultSet set = statement.executeQuery();
		levels = set.getInt(1);
		statement.close();
		
		for (int i = 0; i < levels; i++)
		{
			statement = IO.getConnection().prepareStatement("SELECT state FROM weekly_completed WHERE WeekID = ? AND Level > ? AND Player = ? ORDER BY Level ASC LIMIT 1");
			statement.setInt(1, week);
			statement.setInt(2, i);
			statement.setString(3, player.toString());
			
			set = statement.executeQuery();
			
			if (set.next())
			{
				ChallengeState state = ChallengeState.getByCode(set.getInt(1));
				
				
				switch (state)
				{
				case SUBMITTED:
					waitingLevels.add(i + 1);
					break;
				case DONE:
					approvedLevels.add(i + 1);
					break;
				default:
					rejectedLevels.add(i + 1);
					break;
				}
			}
			else
			{
				notSubmittedLevels.add(i + 1);
			}
			
			statement.close();
		}
		
		if (notSubmittedLevels.size() > 0)
		{
			String line = notSubmittedLevels.size() == 1 ? Settings.getString(Setting.MESSAGE_STATUS_TP_LEVEL) : Settings.getString(Setting.MESSAGE_STATUS_TP_LEVELS);
			
			String numbers = "";
			for (Integer level : notSubmittedLevels)
				numbers += level + ", ";
			numbers = numbers.substring(0, numbers.length() - 2);
			
			line = line.replace("<Numbers>", numbers);
			line = line.replace("<State>", Settings.getString(Setting.MESSAGE_NOT_SUBMITTED));
			playerData += line + " [NEWLINE] ";
		}
		if (waitingLevels.size() > 0)
		{
			String line = waitingLevels.size() == 1 ? Settings.getString(Setting.MESSAGE_STATUS_TP_LEVEL) : Settings.getString(Setting.MESSAGE_STATUS_TP_LEVELS);
			
			String numbers = "";
			for (Integer level : waitingLevels)
				numbers += level + ", ";
			numbers = numbers.substring(0, numbers.length() - 2);
			
			line = line.replace("<Numbers>", numbers);
			line = line.replace("<State>", Settings.getString(Setting.MESSAGE_WAITING_INSPECTION));
			playerData += line + " [NEWLINE] ";
		}
		if (approvedLevels.size() > 0)
		{
			String line = approvedLevels.size() == 1 ? Settings.getString(Setting.MESSAGE_STATUS_TP_LEVEL) : Settings.getString(Setting.MESSAGE_STATUS_TP_LEVELS);
			
			String numbers = "";
			for (Integer level : approvedLevels)
				numbers += level + ", ";
			numbers = numbers.substring(0, numbers.length() - 2);
			
			line = line.replace("<Numbers>", numbers);
			line = line.replace("<State>", Settings.getString(Setting.MESSAGE_COMPLETED));
			playerData += line + " [NEWLINE] ";
		}
		if (rejectedLevels.size() > 0)
		{
			String line = rejectedLevels.size() == 1 ? Settings.getString(Setting.MESSAGE_STATUS_TP_LEVEL) : Settings.getString(Setting.MESSAGE_STATUS_TP_LEVELS);
			
			String numbers = "";
			for (Integer level : rejectedLevels)
				numbers += level + ", ";
			numbers = numbers.substring(0, numbers.length() - 2);
			
			line = line.replace("<Numbers>", numbers);
			line = line.replace("<State>", Settings.getString(Setting.MESSAGE_REJECTED));
			playerData += line + " [NEWLINE] ";
		}
		
		
		return playerData.substring(0, playerData.length() - 11);
	}

}
