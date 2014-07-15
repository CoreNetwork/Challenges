package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.*;


public class LockCommand extends BaseModCommand {
	
	public LockCommand()
	{
		desc = "Lock region to prevent editing";
		needPlayer = true;
		permission = "lock";
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = Challenges.lastTeleport.get(((Player) sender).getName());
			if (id == null)
			{
				Util.Message("Usage: /chm lock [ID]", sender);
				return true;
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_USING_PREVIOUS_ID).replace("<ID>", Integer.toString(id)), sender);
			}
			
		}
		else
			id = Integer.parseInt(args[0]);
		
		boolean autoExpand = false;
		if (args.length > 1 || (args.length > 0 &&!Util.isInteger(args[0])))
			autoExpand = true;
		
		Player player = (Player) sender;
		
		UUID authorUUID = null;
		int week = 0;
		int level = 0;
		String curRegions = null;
		String curWorlds = null;
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Player,State,WeekID,Level,WGRegion,WGWorld FROM weekly_completed WHERE ID = ? LIMIT 1");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				authorUUID = Util.getUUIDFromString(set.getString("Player"));
				level = set.getInt("Level");
				ChallengeState state = ChallengeState.getByCode(set.getInt("State"));
				week = set.getInt("WeekID");
				curRegions = set.getString("WGRegion");
				curWorlds = set.getString("WGWorld");
				
				if (state != ChallengeState.DONE)
				{
					
					Util.Message(Settings.getString(Setting.MESSAGE_LOCK_ONLY_APPROVED).replace("<ID>", Integer.toString(id)), sender);
					set.close();
					statement.close();
                    WorldEditHandler.clearSelection(player);
					return true;
				}
				
				if (week != WeekUtil.getCurrentWeek())
				{
					Util.Message(Settings.getString(Setting.MESSAGE_LOCK_FUTURE_ONLY), sender);
					set.close();
					statement.close();
                    WorldEditHandler.clearSelection(player);
					return true;
				}
				
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_INVALID_ID), sender);
				set.close();
				statement.close();
                WorldEditHandler.clearSelection(player);
				return true;
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
            WorldEditHandler.clearSelection(player);
            return true;
		}
		
		Block[] points = WorldEditHandler.getWorldEditRegion(player, autoExpand);
		WorldEditHandler.clearSelection(player);
		if (points == null)
			return true;
		
		String originalName = "w" + week + "t" + level + "-" + authorUUID.toString();
		String regionName = "w" + week + "t" + level + "-" + authorUUID.toString();

		World world = points[0].getWorld();
		int counter = 2;
		while (WorldGuardManager.regionExists(world, regionName))
		{
			regionName = originalName + "-" + counter;
			counter++;
		}
		
		WorldGuardManager.createRegion(points[0], points[1], regionName);
				
		try
		{			
			if (curRegions == null || curRegions.trim().length() == 0)
			{
				curRegions = regionName;
				curWorlds = world.getName();
			}
			else
			{
				curRegions += "," + regionName;
				curWorlds += "," + world.getName();
			}
			
			PreparedStatement statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET WGRegion = ?, WGWorld = ? WHERE ID = ?");
			statement.setString(1, curRegions);
			statement.setString(2, curWorlds);
			statement.setInt(3, id);
			statement.executeUpdate();
			statement.close();
			
			IO.getConnection().commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		Util.Message(Settings.getString(Setting.MESSAGE_LOCKED), sender);
		return true;
	}

}
