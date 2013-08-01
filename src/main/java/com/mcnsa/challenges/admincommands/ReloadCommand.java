package com.mcnsa.challenges.admincommands;

import org.bukkit.command.CommandSender;

import com.mcnsa.challenges.IO;
import com.mcnsa.challenges.Util;

public class ReloadCommand extends BaseAdminCommand {
	public ReloadCommand()
	{
		needPlayer = false;
		permission = "reload";
	}


	public Boolean run(CommandSender sender, String[] args) {	
		
		IO.LoadSettings();

		Util.Message("&aConfig reloaded successfully!", sender);

	return true;
}

}
