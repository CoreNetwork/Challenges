package com.matejdro.bukkit.mcsna.challenges.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcsna.challenges.IO;
import com.matejdro.bukkit.mcsna.challenges.Util;

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
