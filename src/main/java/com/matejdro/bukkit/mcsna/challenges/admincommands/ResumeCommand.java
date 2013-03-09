package com.matejdro.bukkit.mcsna.challenges.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcsna.challenges.IO;
import com.matejdro.bukkit.mcsna.challenges.Setting;
import com.matejdro.bukkit.mcsna.challenges.Settings;
import com.matejdro.bukkit.mcsna.challenges.Util;
import com.matejdro.bukkit.mcsna.challenges.WeekUtil;

public class ResumeCommand extends BaseAdminCommand {
	
	public ResumeCommand()
	{
		desc = "Stop challenge submissions";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {
		int curWeek = WeekUtil.getCurrentWeek();
		int week = curWeek;
		
		if (args.length > 0 && Util.isInteger(args[0]))
			week = Integer.parseInt(args[0]);
		
		IO.config.set(Setting.STOPPED.getString(), false);
		
		if (curWeek != week)
		{
			IO.config.set(Setting.CURRENT_WEEK.getString(), week);
		}
		
		IO.saveConfig();
		
		Util.Message(Settings.getString(Setting.MESSAGE_RESUMED_ADMIN).replace("<Level>", Integer.toString(week)), sender);
		return true;
	}
	

}