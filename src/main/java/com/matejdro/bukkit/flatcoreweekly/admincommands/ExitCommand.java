package com.matejdro.bukkit.flatcoreweekly.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.flatcoreweekly.EditWizard;
import com.matejdro.bukkit.flatcoreweekly.Setting;
import com.matejdro.bukkit.flatcoreweekly.Settings;
import com.matejdro.bukkit.flatcoreweekly.Util;

public class ExitCommand extends BaseAdminCommand {
	public ExitCommand()
	{
		desc = "Exit editing mode";
		needPlayer = true;
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
