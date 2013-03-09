package com.matejdro.bukkit.flatcoreweekly;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerPoints {
	public static int getPoints(String name)
	{
		int points = 0;
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Points FROM player_points WHERE Player = ? LIMIT 1");
			statement.setString(1, name);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				points = set.getInt("Points");
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return points;
	}
	
	public static void setPoints(String name, Integer points)
	{
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("DELETE FROM player_points WHERE Player = ?");
			statement.setString(1, name);
			statement.executeUpdate();
			statement.close();
			
			statement = IO.getConnection().prepareStatement("INSERT INTO player_points (Player, Points) VALUES (?,?)");
			statement.setString(1, name);
			statement.setInt(2, points);
			statement.executeUpdate();
			statement.close();
			
			IO.getConnection().commit();

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	public static void addPoints(String name, Integer amount, String reason)
	{
		PlayerRank newRank = getNewRank(name, amount);
		if (reason != null && amount < 0)
		{
			if (newRank == null)
			{
				String message = Settings.getString(Setting.MESSAGE_GLOBAL_PUNISHED);
				message = message.replace("<Player>", name);
				message = message.replace("<Points>", Integer.toString(-amount));
				message = message.replace("<Reason>", reason);
				
				Util.Broadcast(message, name);
			}
			else
			{
				String message = Settings.getString(Setting.MESSAGE_GLOBAL_DEMOTED);
				message = message.replace("<Player>", name);
				message = message.replace("<Reason>", reason);
				message = message.replace("<Class>", newRank.rank);
				message = message.replace("<Points>", Integer.toString(-amount));

				Util.Broadcast(message, name);
			}
		}
		else if (newRank != null && amount > 0)
		{
			String message = Settings.getString(Setting.MESSAGE_GLOBAL_PROMOTED);
			message = message.replace("<Player>", name);
			message = message.replace("<Class>", newRank.rank);
			
			Util.Broadcast(message, name);
		}
		
		Player player = Bukkit.getServer().getPlayerExact(name);
		if (player != null)
		{
			addPoints(player, amount, reason, null);
			return;
		}
		
		PreparedStatement statement;
		try {
			statement = IO.getConnection().prepareStatement("INSERT INTO point_changes (Player, Amount, Reason) VALUES (?,?,?)");
			statement.setString(1, name);
			statement.setInt(2, amount);
			statement.setString(3, reason);

			statement.executeUpdate();
			statement.close();
					
			IO.getConnection().commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static PlayerRank getRank(Integer points)
	{
		if (points < 0)
			points = 0;
		
		PlayerRank curRank = null;
		
		for (PlayerRank rank : IO.ranks)
		{
			if (rank.neededPoints <= points)
			{
				curRank = rank;
			}
		}
		
		return curRank;
	}
	
	public static PlayerRank getNextRank(PlayerRank currentRank)
	{
		PlayerRank winner = null;
		
		for (PlayerRank rank : IO.ranks)
		{
			if (rank.neededPoints > currentRank.neededPoints && (winner == null || rank.neededPoints < winner.neededPoints))
			{
				winner = rank;
			}
		}
		
		return winner;
	}
	
	public static void addPoints(Player player, Integer amount, String reason, Integer id)
	{
		int curPoints = getPoints(player.getName());
		PlayerRank oldRank = getRank(curPoints);
		PlayerRank newRank = getRank(curPoints + amount);
		
		if (oldRank != newRank)
		{
			PermissionUser user = PermissionsEx.getUser(player);
			
			boolean dontChange = false;
			for (String group : (List<String>) Settings.getList(Setting.PROTECTED_GROUPS))
			{
				if (user.inGroup(group))
				{
					dontChange = true;
					break;
				}
			}
			
			if (!dontChange)
			{
				user.removeGroup(oldRank.group);
				user.addGroup(newRank.group);
				user.setSuffix(newRank.suffix, null);
			}
			
			String message;
			if (amount > 0)
			{
				if (reason == null)
				{
					message = Settings.getString(Setting.MESSAGE_PROMOTED);
				}
				else
				{
					message = Settings.getString(Setting.MESSAGE_PROMOTED_REASON);
					message = message.replace("<Reason>", reason);
				}
			}
			else
			{
				if (reason == null)
				{
					message = Settings.getString(Setting.MESSAGE_DEMOTED);
				}
				else
				{
					message = Settings.getString(Setting.MESSAGE_DEMOTED_REASON);
					message = message.replace("<Reason>", reason);
				}
			}
			
			
			message = message.replace("<Class>", newRank.rank);
			Util.Message(message, player);
		}
		else if (amount > 0)
		{
			String message;
			if (reason == null)
			{
				message = Settings.getString(Setting.MESSAGE_EARNED_POINTS);
			}
			else
			{
				message = Settings.getString(Setting.MESSAGE_EARNED_POINTS_REASON);
				message = message.replace("<Reason>", reason);
			}
			
			message = message.replace("<Amount>", Integer.toString(amount));
			Util.Message(message, player);

		}
		else
		{
			String message;
			if (reason == null)
			{
				message = Settings.getString(Setting.MESSAGE_PUNISHED);
			}
			else
			{
				message = Settings.getString(Setting.MESSAGE_PUNISHED_REASON);
				message = message.replace("<Reason>", reason);
			}
			
			message = message.replace("<Amount>", Integer.toString(-amount));
			Util.Message(message, player);
		}
		
		setPoints(player.getName(), getPoints(player.getName()) + amount);
		
		if (id != null)
		{
			try
			{
				PreparedStatement statement = IO.getConnection().prepareStatement("DELETE FROM point_changes WHERE ID = ?");
				statement.setInt(1, id);
				statement.executeUpdate();
				statement.close();
				
				IO.getConnection().commit();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static PlayerRank getNewRank(String player, Integer change)
	{
		int curPoints = getPoints(player);
		PlayerRank currentRank = getRank(curPoints);
		PlayerRank newRank = getRank(curPoints + change);
				
		return currentRank != newRank ? newRank : null;
	}
	
	
}
