package us.corenetwork.challenges.admincommands;

import org.bukkit.command.CommandSender;

import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Util;


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
