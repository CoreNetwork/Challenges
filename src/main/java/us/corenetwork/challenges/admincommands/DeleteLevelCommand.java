package us.corenetwork.challenges.admincommands;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.EditWizard;
import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class DeleteLevelCommand extends BaseAdminCommand {
	public DeleteLevelCommand()
	{
		desc = "Delete level";
		needPlayer = true;
		permission = "delete";
	}


	public Boolean run(CommandSender sender, String[] args) {	
		EditWizard.PlayerData data = EditWizard.players.get(((Player) sender).getName());
		
		if (data == null)
		{
			Util.Message(Settings.getString(Setting.MESSAGE_NOT_IN_EDIT_MODE), sender);
			return true;
		}
		
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			Util.Message("Usage: /cha dellevel [level]", sender);
			return true;
		}
		
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("DELETE FROM weekly_levels WHERE WeekID = ? AND Level = ?");
			statement.setInt(1, data.weekId);
			statement.setInt(2, Integer.parseInt(args[0]));
			statement.executeUpdate();
			statement.close();
			
			Util.Message(Settings.getString(Setting.MESSAGE_LEVEL_DELETED), sender);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
}
