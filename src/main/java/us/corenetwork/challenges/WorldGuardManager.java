package us.corenetwork.challenges;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardManager {

	public static void createRegion(Block firstPoint, Block secondPoint, String name)
	{
		name = name.toLowerCase();
		
		WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		RegionManager manager = wg.getRegionManager(firstPoint.getWorld());
		
		BlockVector firstVector = new BlockVector(firstPoint.getX(), firstPoint.getY(), firstPoint.getZ());
		BlockVector secondVector = new BlockVector(secondPoint.getX(), secondPoint.getY(), secondPoint.getZ());
	
		ProtectedRegion region = new ProtectedCuboidRegion(name, firstVector, secondVector);
		region.setFlag(DefaultFlag.BUILD, StateFlag.State.DENY);
		region.setFlag(DefaultFlag.CHEST_ACCESS, StateFlag.State.DENY);
		region.setFlag(DefaultFlag.PISTONS, StateFlag.State.DENY);
		region.setFlag(DefaultFlag.USE, StateFlag.State.DENY);
        region.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
        region.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
        region.setFlag(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, StateFlag.State.DENY);
        region.setFlag(DefaultFlag.ENTITY_PAINTING_DESTROY, StateFlag.State.DENY);
        region.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, StateFlag.State.DENY);
		manager.addRegion(region);
		try
		{
			manager.save();
		} catch (StorageException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean regionExists(World world, String name)
	{
		name = name.toLowerCase();

		WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		RegionManager manager = wg.getRegionManager(world);
		return manager.getRegion(name) != null;
	}
	
	public static void deleteRegion(World world, String name)
	{
		name = name.toLowerCase();

		WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		RegionManager manager = wg.getRegionManager(world);
		
		if (manager.getRegion(name) == null)
		{
			Challenges.log.warning("[Challenges] Trying to delete nonexistent region in world " + world + "! Name: " + name);
		}
		
		manager.removeRegion(name);
		try
		{
			manager.save();
		} catch (StorageException e)
		{
			e.printStackTrace();
		}
	}
	
}
