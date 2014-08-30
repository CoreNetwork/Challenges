package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.*;


public class TpCommand extends BaseModCommand {
	
	public TpCommand()
	{
		desc = "Teleport to completed level";
		needPlayer = true;
		permission = "tp";
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = Challenges.lastTeleport.get(((Player) sender).getName());
			if (id == null)
			{
				Util.Message("Usage: /chm tp [ID]", sender);
				return true;
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_USING_PREVIOUS_ID).replace("<ID>", Integer.toString(id)), sender);
			}
			
		}
		else
			id = Integer.parseInt(args[0]);
		
		UnclaimCommand.unclaimPlayer(((Player) sender).getUniqueId());
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT X,Y,Z,WORLD,ClaimedBy,Player,WeekId,State FROM weekly_completed WHERE ID = ? LIMIT 1");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				World world = Bukkit.getServer().getWorld(set.getString("World"));
				int x = set.getInt("X");
				int y = set.getInt("Y");
				int z = set.getInt("Z");

				ChallengeState state = ChallengeState.getByCode(set.getInt("State"));
				
                UUID claimedBy = Util.getUUIDFromString(set.getString("ClaimedBy"));
                String modName = Util.getPlayerNameFromUUID(claimedBy);
                if (claimedBy != null && !claimedBy.equals(((Player) sender).getUniqueId()))
				{
					Util.Message(Settings.getString(Setting.MESSAGE_ALREADY_HANDLED).replace("<Mod>", modName), sender);
				}
				else
				{
					
					final Location loc = new Location(world, x, y, z);
					Chunk c = loc.getChunk();
					if (!c.isLoaded())
						loc.getChunk().load();
					final Player player = (Player) sender;
					player.teleport(loc);
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Challenges.instance, new Runnable() {
						@Override
						public void run() {
							player.teleport(loc);
							
						}
					}, 10);
					
					
					Challenges.lastTeleport.put(player.getName(), id);
					
					String message = Settings.getString(Setting.MESSAGE_TELEPORTED);
					message = message.replace("<ID>", Integer.toString(id));
					
					Util.Message(message, sender);
					
					UUID challengeOwner = Util.getUUIDFromString(set.getString("Player"));
					int week = set.getInt("WeekId");
					Util.Message(getPlayerDataString(challengeOwner, week), sender);
					
					try
					{
						if (state == ChallengeState.SUBMITTED) {
							PreparedStatement statement2 = IO.getConnection().prepareStatement("UPDATE weekly_completed SET ClaimedBy=? WHERE ID = ?");
							statement2.setString(1, player.getName());
							statement2.setInt(2, id);
							statement2.executeUpdate();
							statement2.close();

							IO.getConnection().commit();
						}
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}

				}
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
