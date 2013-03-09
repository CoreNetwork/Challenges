package com.matejdro.bukkit.mcsna.challenges.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcsna.challenges.IO;
import com.matejdro.bukkit.mcsna.challenges.Setting;
import com.matejdro.bukkit.mcsna.challenges.Settings;
import com.matejdro.bukkit.mcsna.challenges.Util;

public class StopCommand extends BaseAdminCommand {
	
	public StopCommand()
	{
		desc = "Stop challenge submissions";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {		
		IO.config.set(Setting.STOPPED.getString(), true);
		IO.saveConfig();
		
		Util.Message(Settings.getString(Setting.MESSAGE_STOPPED_ADMIN), sender);
		return true;
	}
	

}
