package com.matejdro.bukkit.flatcoreweekly.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.flatcoreweekly.IO;
import com.matejdro.bukkit.flatcoreweekly.Util;

public class ReloadCommand extends BaseAdminCommand {
	public ReloadCommand()
	{
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {	
		
		IO.LoadSettings();

		Util.Message("&aConfig reloaded successfully!", sender);

	return true;
}

}
