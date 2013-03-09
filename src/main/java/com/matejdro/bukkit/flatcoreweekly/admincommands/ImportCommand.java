package com.matejdro.bukkit.flatcoreweekly.admincommands;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.flatcoreweekly.FlatcoreWeekly;
import com.matejdro.bukkit.flatcoreweekly.IO;
import com.matejdro.bukkit.flatcoreweekly.Util;

public class ImportCommand extends BaseAdminCommand {
	public ImportCommand()
	{
		desc = "Import data from old persistance DB";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {	

		File db = new File(FlatcoreWeekly.instance.getDataFolder(), "persistance.db");
		if (!db.exists())
		{
			Util.Message("&cpersistance.db does not exists!", sender);
			return true;
		}
		
		Util.Message("&6Importing... Please wait! (Server might freeze for several seconds)", sender);

		HashMap<String, Integer> allPlayers = new HashMap<String, Integer>();
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" +  db.getPath());
			conn.setAutoCommit(false);

			PreparedStatement statement = conn.prepareStatement("SELECT * FROM persistance WHERE key LIKE 'storage.challengePoints%'");
			ResultSet set = statement.executeQuery();

			while (set.next())
			{
				String name = set.getString("key").split("\\.")[2];
				Integer points = Integer.parseInt(set.getString("value").replace("\"", ""));
				
				allPlayers.put(name, points);
			}

			statement.close();
			conn.close();

			PreparedStatement delStatement = IO.getConnection().prepareStatement("DELETE FROM player_points WHERE Player = ?");
			statement = IO.getConnection().prepareStatement("INSERT INTO player_points (Player, Points) VALUES (?,?)");

			for (Entry<String, Integer> e : allPlayers.entrySet())
			{
				String name = e.getKey();
				Integer points = e.getValue();
				
				delStatement.setString(1, name);
				
				statement.setString(1, name);
				statement.setInt(2, points);
				
				statement.addBatch();
				delStatement.addBatch();

			}
			
			delStatement.executeBatch();
			delStatement.close();
			statement.executeBatch();
			statement.close();
			
			IO.getConnection().commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		Util.Message("&aData imported successfully!", sender);

	return true;
}

}
