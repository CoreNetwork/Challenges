package com.mcnsa.challenges.admincommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcnsa.challenges.EditWizard;
import com.mcnsa.challenges.IO;
import com.mcnsa.challenges.Setting;
import com.mcnsa.challenges.Settings;
import com.mcnsa.challenges.Util;

public class EditLevelCommand extends BaseAdminCommand {
	public EditLevelCommand()
	{
		desc = "Edit/Create level";
		needPlayer = true;
		permission = "edit";
	}


	public Boolean run(CommandSender sender, String[] args) {	
		
		EditWizard.PlayerData data = EditWizard.players.get(((Player) sender).getName());
		
		if (data == null)
		{
			Util.Message(Settings.getString(Setting.MESSAGE_NOT_IN_EDIT_MODE), sender);
			return true;
		}
		else if (data.state != EditWizard.State.WAITING)
		{
			Util.Message(Settings.getString(Setting.MESSAGE_FINISH_EDITING_FIRST), sender);
			return true;
		}
		
		int selectedLevel = 1;
		
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			PreparedStatement statement;
			try {
				statement = IO.getConnection().prepareStatement("SELECT Level FROM weekly_levels WHERE WeekID = ? ORDER BY Level DESC LIMIT 1");
				statement.setInt(1, data.weekId);
				ResultSet set = statement.executeQuery();
				if (set.next())
				{
					selectedLevel = set.getInt("Level") + 1;
				}
				set.close();
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			selectedLevel = Integer.parseInt(args[0]);
				
		data.level = selectedLevel;
		
		PreparedStatement statement;
		try {
			statement = IO.getConnection().prepareStatement("SELECT Description, Points FROM weekly_levels WHERE WeekID = ? AND Level = ? LIMIT 1");
			statement.setInt(1, data.weekId);
			statement.setInt(2, data.level);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				data.description = set.getString("Description");
				data.points = set.getInt("Points");
				Util.Message(Settings.getString(Setting.MESSAGE_EDITING_LEVEL).replace("<Number>", Integer.toString(selectedLevel)), sender);				
			}
			else
			{
				data.points = null;
				data.description = null;
				Util.Message(Settings.getString(Setting.MESSAGE_CREATING_LEVEL).replace("<Number>", Integer.toString(selectedLevel)), sender);
			}
			set.close();
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EditWizard.startEnteringDescription(((Player) sender));
		
		return true;
	}
	
}
