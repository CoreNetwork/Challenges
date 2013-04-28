package com.matejdro.bukkit.mcnsa.challenges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChallengesListener implements Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(final AsyncPlayerChatEvent event)
	{
		if (EditWizard.players.containsKey(event.getPlayer().getName()))
		{
			if (EditWizard.chatEvent(event))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event)
	{			
		if (event.getPlayer().hasPermission("mcnsachallenges.notify"))
		{
			try {
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT ID FROM weekly_completed WHERE State = 0");
				ResultSet set = statement.executeQuery();
				if (set.next())
				{
					Util.Message(Settings.getString(Setting.MESSAGE_MOD_LOGIN_NOTICE), event.getPlayer());
				}
				
				set.close();
				statement.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT ID, Level, ModResponse FROM weekly_completed WHERE State = 2 AND Player = ? GROUP BY WeekID");
			statement.setString(1, event.getPlayer().getName());
			ResultSet set = statement.executeQuery();
			while (set.next())
			{
				String level = Integer.toString(set.getInt("Level"));
				String message = set.getString("ModResponse");
				int id = set.getInt("ID");
				if (message == null || message.trim().equals(""))
					Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_REJECTED).replace("<Level>", level), event.getPlayer());
				else
					Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_REJECTED_MESSAGE).replace("<Message>", message).replace("<Level>", level), event.getPlayer());
				
				PreparedStatement delStatement = IO.getConnection().prepareStatement("DELETE FROM weekly_completed WHERE ID = ?");
				delStatement.setInt(1, id);
				delStatement.executeUpdate();
				delStatement.close();
				
				IO.getConnection().commit();

			}
			
			statement.close();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT * FROM point_changes WHERE Player = ?");
			statement.setString(1, event.getPlayer().getName());
			ResultSet set = statement.executeQuery();
			while (set.next())
			{
				PlayerPoints.addPoints(event.getPlayer(), set.getInt("Amount"), set.getString("Reason"), set.getInt("ID"));
			}
			
			statement.close();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		if (event.getPlayer().hasPermission("mcnsachallenges.command.chm"))
		{
			try
			{
				PreparedStatement statement2 = IO.getConnection().prepareStatement("UPDATE weekly_completed SET ClaimedBy=NULL WHERE ClaimedBy = ?");
				statement2.setString(1, event.getPlayer().getName());
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
}
