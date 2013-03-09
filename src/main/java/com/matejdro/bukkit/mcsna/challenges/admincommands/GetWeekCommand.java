package com.matejdro.bukkit.mcsna.challenges.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcsna.challenges.Util;
import com.matejdro.bukkit.mcsna.challenges.WeekUtil;

public class GetWeekCommand extends BaseAdminCommand {
	
	public GetWeekCommand()
	{
		desc = "[DEBUG] Get current week ID";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Util.Message(String.valueOf(WeekUtil.getCurrentWeek()), sender);
		return true;
	}
	

}
