package us.corenetwork.challenges.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.EditWizard;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class SaveCommand extends BaseAdminCommand {
	public SaveCommand()
	{
		desc = "Exit editing mode";
		needPlayer = true;
		permission = "exit";
	}


	public Boolean run(CommandSender sender, String[] args) {	
		
		if (EditWizard.players.containsKey(((Player) sender).getName()))
		{
			EditWizard.players.remove(((Player) sender).getName());
			Util.Message(Settings.getString(Setting.MESSAGE_EDITING_MODE_EXIT), sender);
		}
		else
			Util.Message(Settings.getString(Setting.MESSAGE_NOT_IN_EDIT_MODE), sender);

		return true;
	}
	
}
