package com.matejdro.bukkit.mcnsa.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.mcnsa.challenges.IO;
import com.matejdro.bukkit.mcnsa.challenges.MCNSAChallenges;
import com.matejdro.bukkit.mcnsa.challenges.Setting;
import com.matejdro.bukkit.mcnsa.challenges.Settings;
import com.matejdro.bukkit.mcnsa.challenges.Util;

public class TpCommand extends BaseModCommand {
	
	public TpCommand()
	{
		desc = "Teleport to completed level";
		needPlayer = true;
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = MCNSAChallenges.lastTeleport.get(((Player) sender).getName());
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
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT X,Y,Z,WORLD,ClaimedBy FROM weekly_completed WHERE ID = ? LIMIT 1");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				World world = Bukkit.getServer().getWorld(set.getString("World"));
				int x = set.getInt("X");
				int y = set.getInt("Y");
				int z = set.getInt("Z");
				
				String claimedBy = set.getString("ClaimedBy");
				if (claimedBy != null && !claimedBy.equals(((Player) sender).getName()))
				{
					Util.Message(Settings.getString(Setting.MESSAGE_ALREADY_HANDLED).replace("<Mod>", claimedBy), sender);
				}
				else
				{
					
					final Location loc = new Location(world, x, y, z);
					Chunk c = loc.getChunk();
					if (!c.isLoaded())
						loc.getChunk().load();
					final Player player = (Player) sender;
					player.teleport(loc);
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MCNSAChallenges.instance, new Runnable() {
						@Override
						public void run() {
							player.teleport(loc);
							
						}
					}, 10);
					
					
					MCNSAChallenges.lastTeleport.put(player.getName(), id);
					
					String message = Settings.getString(Setting.MESSAGE_TELEPORTED);
					message = message.replace("<ID>", Integer.toString(id));
					
					Util.Message(message, sender);
					
					try
					{
						PreparedStatement statement2 = IO.getConnection().prepareStatement("UPDATE weekly_completed SET ClaimedBy=? WHERE ID = ?");
						statement2.setString(1, player.getName());
						statement2.setInt(2, id);
						statement2.executeUpdate();
						statement2.close();
						
						IO.getConnection().commit();
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
            MCNSAChallenges.log.log(Level.SEVERE, "[FlatcoreWeekly]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

}
