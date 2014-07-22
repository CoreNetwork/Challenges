package us.corenetwork.challenges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
//import java.util.logging.Handler;
import java.util.logging.Level;
//import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import us.corenetwork.challenges.admincommands.*;
import us.corenetwork.challenges.admincommands.SaveCommand;
import us.corenetwork.challenges.modcommands.*;
import us.corenetwork.challenges.usercommands.AllCommand;
import us.corenetwork.challenges.usercommands.BaseUserCommand;
import us.corenetwork.challenges.usercommands.ChCommand;
import us.corenetwork.challenges.usercommands.DoneCommand;
import us.corenetwork.challenges.usercommands.PointsCommand;
import us.corenetwork.challenges.usercommands.StatusCommand;
import us.corenetwork.challenges.usercommands.TopCommand;
import us.corenetwork.challenges.usercommands.UserHelpCommand;


public class Challenges extends JavaPlugin {
	public static Logger log;

	private ChallengesListener listener;
	
	public static Challenges instance;

	public static Permission permission;
	public static Chat chat;
	
	public static HashMap<String, BaseModCommand> modCommands = new HashMap<String, BaseModCommand>();
	public static HashMap<String, BaseAdminCommand> adminCommands = new HashMap<String, BaseAdminCommand>();
	public static HashMap<String, BaseUserCommand> userCommands = new HashMap<String, BaseUserCommand>();
	public static BaseUserCommand chCommand = new ChCommand();
	
	public static HashMap<String, Integer> lastTeleport = new HashMap<String, Integer>();


	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}

	@Override
	public void onDisable() {
		IO.freeConnection();
	}

	@Override
	public void onEnable() {
		instance = this;
		listener = new ChallengesListener();
		log = getLogger();
		IO.LoadSettings();
		IO.PrepareDB();

		if (!setupPermissions())
		{
			getLogger().warning("could not load Vault permissions - did you forget to install Vault?");
		}

		if (!setupChat())
		{
			getLogger().warning("could not load Vault chat - did you forget to install Vault?");
		}

		getServer().getPluginManager().registerEvents(listener, this);
		
		//Init timers
		//getServer().getScheduler().scheduleSyncRepeatingTask(this, new TimeEvent(), 20, 20);

		//User commands
		userCommands.put("help", new UserHelpCommand());
		userCommands.put("all", new AllCommand());
		userCommands.put("done", new DoneCommand());
		userCommands.put("points", new PointsCommand());
		userCommands.put("status", new StatusCommand());
		userCommands.put("top", new TopCommand());

		//Admin commands
		adminCommands.put("help", new AdminHelpCommand());
		adminCommands.put("list", new ListCommand());
		adminCommands.put("edit", new EditWeekCommand());
		adminCommands.put("create", new EditWeekCommand());
		adminCommands.put("editlevel", new EditLevelCommand());
		adminCommands.put("createlevel", new EditLevelCommand());
		adminCommands.put("deletelevel", new DeleteLevelCommand());
		adminCommands.put("save", new SaveCommand());
		adminCommands.put("reload", new ReloadCommand());
		adminCommands.put("stop", new StopCommand());
		adminCommands.put("resume", new ResumeCommand());

		//Mod Commands
		modCommands.put("help", new ModHelpCommand());
		modCommands.put("list", new CompletedListCommand());
		modCommands.put("tp", new TpCommand());
		modCommands.put("complete", new CompleteCommand());
		modCommands.put("deny", new DenyCommand());
		modCommands.put("lock", new LockCommand());
		modCommands.put("points", new ModPointsCommand());
		modCommands.put("explode", new ExplodeCommand());
		modCommands.put("unclaim", new UnclaimCommand());
		modCommands.put("undo", new UncompleteCommand());
		modCommands.put("top", new ModTopCommand());
		modCommands.put("history", new HistoryCommand());
		modCommands.put("blame", new BlameCommand());
		modCommands.put("all", new PrintAllSubmissionsCommand());

		//DEBUG COMMANDS
		
		if (Settings.getBoolean(Setting.DEBUG_MODE))
		{
			adminCommands.put("settime", new SetTimeCommand());
		}
		
		log.info(getDescription().getFullName() + " loaded!");
		
		Bukkit.getServer().getScheduler().runTask(Challenges.instance, new WeekAnnouncer());
		
		try
		{
			PreparedStatement statement2 = IO.getConnection().prepareStatement("UPDATE weekly_completed SET ClaimedBy=NULL");
			statement2.executeUpdate();
			statement2.close();
			
			IO.getConnection().commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
		

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getName().equals("ok"))
		{
			return EditWizard.doneEvent(sender);
		}
		else if (command.getName().equals("ch")) //User command
		{
			if (args.length < 1 || Util.isInteger(args[0]))
				return chCommand.execute(sender, args);
			
			BaseUserCommand cmd = userCommands.get(args[0]);
			if (cmd != null)
				return cmd.execute(sender, args);
			
		}
		else if (command.getName().equals("chm")) //Mod command
		{
			if (args.length < 1 || Util.isInteger(args[0]))
				return modCommands.get("help").execute(sender, args);
			
			BaseModCommand cmd = modCommands.get(args[0]);
			if (cmd != null)
				return cmd.execute(sender, args);
				
		}
		else if (command.getName().equals("cha")) //Admin command
		{
			if (args.length < 1 || Util.isInteger(args[0]))
				return adminCommands.get("help").execute(sender, args);
			
			BaseAdminCommand cmd = adminCommands.get(args[0]);
			if (cmd != null)
				return cmd.execute(sender, args);
				
		}
		else //flatpoints command
		{
			BaseUserCommand cmd = userCommands.get("points");
			return cmd.execute(sender, args);
		}
		return true;
	}
	
	private static class WeekAnnouncer extends BukkitRunnable
	{

		@Override
		public void run() {
			Bukkit.getScheduler().runTaskLater(Challenges.instance, this, getNextTime());
			
			int curWeek = WeekUtil.getCurrentWeek();
			
			if (WeekUtil.getWeekStart(curWeek + 1) < System.currentTimeMillis() / 1000)
			{
				curWeek++;
				Challenges.log.info("New week " + curWeek + "!");
				YamlConfiguration config = SettingType.STORAGE.getConfig();
				config.set(Setting.CURRENT_WEEK.getString(), curWeek);
				config.set(Setting.CURRENT_WEEK_START.getString(), WeekUtil.getWeekStart(curWeek + 1)); // + 1 because the offset is needed.
				IO.saveConfig();

				Util.Broadcast(Settings.getString(Setting.MESSAGE_NEW_CHALLENGE_ANNOUNCEMENT), "");
						
				try {
					PreparedStatement statement = IO.getConnection().prepareStatement("SELECT WGRegion, WGWorld FROM weekly_completed WHERE WGRegion IS NOT NULL AND WeekID < ?");
					statement.setInt(1, curWeek);
					ResultSet set = statement.executeQuery();
					while (set.next())
					{
						String regionString = set.getString("WGRegion");
						String[] regions = regionString.split(",");
						
						String worldsString = set.getString("WGWorld");
						String[] worlds = worldsString.split(",");

						for (int i = 0; i < regions.length; i++)
						{
							String region = regions[i];
							if (region != null && !region.trim().equals(""))
							{
								String worldName;
								
								worldName = worlds[i];
																
								World world = Bukkit.getServer().getWorld(worldName);
								WorldGuardManager.deleteRegion(world, region);
							}
						}
						
					}
					
					set.close();
					statement.close();
					
					statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET WGRegion = NULL, WGWorld = NULL WHERE WeekID < ?");
					statement.setInt(1, curWeek);
					statement.executeUpdate();
					
					statement.close();
					IO.getConnection().commit();
				}
				catch (SQLException e) {
		            Challenges.log.log(Level.SEVERE, "Error while running list command! - " + e.getMessage(), e);
					e.printStackTrace();
				}

			}
		}
		
		private static long getNextTime()
		{
			DateTime nextWeekStart = new DateTime().withMillis(WeekUtil.getWeekStart(WeekUtil.getCurrentWeek() + 1) * 1000);
			long timeLeft = nextWeekStart.getMillis() - System.currentTimeMillis();
			timeLeft /= 1000; // convert to seconds
			if (timeLeft < 5)
				return 1;
			else if (timeLeft < 20)
				return 40;
			else if (timeLeft < 120)
				return 200;
			else 
				return 600;
		}
		
	}
}
