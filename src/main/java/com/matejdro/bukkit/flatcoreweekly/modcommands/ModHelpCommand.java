package com.matejdro.bukkit.flatcoreweekly.modcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.flatcoreweekly.FlatcoreWeekly;
import com.matejdro.bukkit.flatcoreweekly.Settings;
import com.matejdro.bukkit.flatcoreweekly.Util;

public class ModHelpCommand extends BaseModCommand {
	
	public ModHelpCommand()
	{
		desc = "List all possible commands";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {		
		int page = 1;
		if (args.length > 0 && Util.isInteger(args[0])) page = Integer.parseInt(args[0]);
		List<String> komandes = new ArrayList<String>();

		for (Entry<String, BaseModCommand> e : FlatcoreWeekly.modCommands.entrySet())
		{
			komandes.add(Settings.getCommandDescription(e.getKey(), "chm"));
		}  		
		String[] komande = komandes.toArray(new String[0]);
		Arrays.sort(komande);
		
		int maxpage = (int) Math.ceil((double) komande.length / (sender instanceof Player ? 15.0 : 30.0));
		
		if (page > maxpage)
			page = maxpage;
		
		Util.Message("List of all commands:", sender);
		Util.Message("§8Page " + String.valueOf(page) + " of " + String.valueOf(maxpage), sender);

		for (int i = (page - 1) * 15; i < page * 15; i++)
		{
			if (komande.length < i + 1 || i < 0) break;	
			Util.Message(komande[i], sender);
		}   		
		return true;
	}
	

}
