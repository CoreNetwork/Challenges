package com.matejdro.bukkit.mcnsa.challenges.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcnsa.challenges.IO;
import com.matejdro.bukkit.mcnsa.challenges.Setting;
import com.matejdro.bukkit.mcnsa.challenges.Settings;
import com.matejdro.bukkit.mcnsa.challenges.Util;

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
