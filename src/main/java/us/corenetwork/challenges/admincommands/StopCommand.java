package us.corenetwork.challenges.admincommands;

import org.bukkit.command.CommandSender;

import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class StopCommand extends BaseAdminCommand {
	
	public StopCommand()
	{
		desc = "Stop challenge submissions";
		needPlayer = false;
		permission = "stop";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		IO.config.set(Setting.STOPPED.getString(), true);
		IO.saveConfig();
		
		Util.Message(Settings.getString(Setting.MESSAGE_STOPPED_ADMIN), sender);
		return true;
	}
	

}
