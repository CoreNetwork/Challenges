package com.matejdro.bukkit.flatcoreweekly.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.flatcoreweekly.Util;
import com.matejdro.bukkit.flatcoreweekly.WeekUtil;

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
