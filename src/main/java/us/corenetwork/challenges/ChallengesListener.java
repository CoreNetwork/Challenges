package us.corenetwork.challenges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import us.corenetwork.challenges.modcommands.UnclaimCommand;


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
	public void onJoin(final PlayerJoinEvent event)
	{			
		if (Util.hasPermission(event.getPlayer(), "challenges.notify"))
		{
			try {
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT COUNT(*) FROM weekly_completed WHERE State = ?");
				statement.setInt(1, ChallengeState.SUBMITTED.code());
				ResultSet set = statement.executeQuery();
				
				set.next();
				int amount = set.getInt(1);
				
				if (amount > 0)
				{
					String message = Settings.getString(Setting.MESSAGE_MOD_LOGIN_NOTICE);
					message = message.replace("<Amount>", Integer.toString(amount));
					Util.Message(message, event.getPlayer());
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
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT ID, Level, ModResponse, WeekID FROM weekly_completed WHERE State = 2 AND Player = ? GROUP BY WeekID");
			statement.setString(1, event.getPlayer().getUniqueId().toString());
			ResultSet set = statement.executeQuery();
            PreparedStatement delStatement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET state = 3 WHERE WeekID = ? AND Player = ? AND State = 2");
			while (set.next())
			{
				String level = Integer.toString(set.getInt("Level"));
				String message = set.getString("ModResponse");
				int weekId = set.getInt("WeekID");
				if (message == null || message.trim().equals(""))
					Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_REJECTED).replace("<Level>", level), event.getPlayer());
				else
					Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_REJECTED_MESSAGE).replace("<Message>", message).replace("<Level>", level), event.getPlayer());

				delStatement.setInt(1, weekId);
				delStatement.setString(2, event.getPlayer().getUniqueId().toString());
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
		
		Bukkit.getScheduler().runTask(Challenges.instance, new Runnable() {
			@Override
			public void run() {
				try
				{
					PreparedStatement statement = IO.getConnection().prepareStatement("SELECT * FROM point_changes WHERE Player = ?");
					statement.setString(1, event.getPlayer().getUniqueId().toString());
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
		});

	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		if (Util.hasPermission(event.getPlayer(), "challenges.unclaim"))
		{
			UnclaimCommand.unclaimPlayer(event.getPlayer().getUniqueId());
		}
		
	}
}
