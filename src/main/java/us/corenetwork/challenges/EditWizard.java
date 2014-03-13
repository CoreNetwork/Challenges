package us.corenetwork.challenges;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EditWizard {
	public static HashMap<String, PlayerData> players = new HashMap<String, PlayerData>();
	
	
	public static void startWeek(String player, int weekId)
	{
		PlayerData data = new PlayerData();
		data.weekId = weekId;
		data.state = State.WAITING;
		
		players.put(player, data);
	}
	
	public static boolean doneEvent(CommandSender sender)
	{
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		PlayerData data = players.get(player.getName());
		if (data == null)
			return false;
		
		if (data.state == State.DESCRIPTION)
		{
			Util.Message(Settings.getString(Setting.MESSAGE_ENTER_POINTS), player);
			
			if (data.points != null)
			{
				Util.Message(Settings.getString(Setting.MESSAGE_OLD_POINTS).replace("<Number>", Integer.toString(data.points)), player);
			}

			data.state = State.POINTS;
			return true;
		}
		
		return false;
	}
	
	public static boolean chatEvent(AsyncPlayerChatEvent event)
	{
		PlayerData data = players.get(event.getPlayer().getName());
		switch (data.state)
		{
			case DESCRIPTION:
				enterDescription(event.getPlayer(), data, event.getMessage());
				return true;
			case POINTS:
				enterPoints(event.getPlayer(), data, event.getMessage());
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	public static void startEnteringDescription(Player player)
	{
		PlayerData data = players.get(player.getName());
		if (data == null) return;
				
		Util.Message(Settings.getString(Setting.MESSAGE_ENTER_DESCRIPTION), player);
		
		if (data.description != null)
		{
			Util.Message(Settings.getString(Setting.MESSAGE_OLD_DESCRITPION).replace("<Desc>", data.description), player);
		}
		
		data.state = EditWizard.State.DESCRIPTION;
		data.firstDesc = true;
	}
	
	public static void enterDescription(Player player, PlayerData data, String desc)
	{
		if (data.firstDesc)
		{
			data.firstDesc = false;
			data.description = "";
		}
		
		data.description += desc;
		
		Util.Message(Settings.getString(Setting.MESSAGE_DESCRPITION_PART_ENTERED), player);
		
	}
	
	public static void enterPoints(Player player, PlayerData data, String pointsS)
	{
		if (!pointsS.trim().equals("-1"))
		{
			if (!Util.isInteger(pointsS))
			{
				Util.Message(Settings.getString(Setting.MESSAGE_MUST_ENTER_NUMBER_POINTS), player);
			}
			
			data.points = Integer.parseInt(pointsS);
		}
		else if (data.points == null)
		{
			Util.Message(Settings.getString(Setting.MESSAGE_MUST_ENTER_NUMBER_POINTS), player);
			return;
		}
		
		finalize(player, data);
	}
	
	public static void finalize(Player player, PlayerData data)
	{
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("DELETE FROM weekly_levels WHERE WeekID = ? AND Level = ?");
			statement.setInt(1, data.weekId);
			statement.setInt(2, data.level);
			statement.executeUpdate();
			statement.close();
			statement = IO.getConnection().prepareStatement("INSERT INTO weekly_levels (WeekID, Level, Description, Points) VALUES (?,?,?,?)");
			statement.setInt(1, data.weekId);
			statement.setInt(2, data.level);
			statement.setString(3, data.description);
			statement.setInt(4, data.points);
			statement.executeUpdate();
			statement.close();
			IO.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		data.state = State.WAITING;
		Util.Message(Settings.getString(Setting.MESSAGE_LEVEL_SAVED), player);
		
	}
	
	public static class PlayerData
	{
		public int weekId;
		
		public State state;
		public int level;
		public boolean firstDesc = true;
	
		public String description;
		public Integer points;
	}
	
	public static enum State
	{
		WAITING,
		DESCRIPTION,
		POINTS
		
	}
}