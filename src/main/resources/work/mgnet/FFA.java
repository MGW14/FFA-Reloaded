package work.mgnet;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import work.mgnet.commands.FFAConfigCommand;
import work.mgnet.commands.ForceendCommand;
import work.mgnet.commands.ForcestartCommand;
import work.mgnet.commands.ItemsCommand;
import work.mgnet.commands.ReadyCommand;
import work.mgnet.commands.ReloadmapCommand;
import work.mgnet.commands.SetItemsCommand;
import work.mgnet.commands.SetKitCommand;
import work.mgnet.commands.StatisticsCommand;
import work.mgnet.utils.ConfigurationUtils;
import work.mgnet.utils.KitUtils;
import work.mgnet.utils.SoundsUtils;
import work.mgnet.utils.StatsUtils;

@Plugin(id = "ffa", name = "FFA", version = "1.0", description = "Adds FFA")
public class FFA {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir; // Given Config Direction 

	public static String selectedKit = "default"; // Currently Selected Kit
	private static Path configDir; // Public Config Direction
	private static File mapFile; // Public Map Schematic File
	
	public static ConfigurationUtils configUtils; //Configuration Utils
	public static StatsUtils statsUtils; // Stats Utils
	
	public static HashMap<String, String> edit = new HashMap<String, String>(); // List for /setitems
	
	/**
	 * Get The Selected Map File
	 * @return Returns the Schematic File of the Currently Selected Map
	 */
	public static File getMapFile() {
		return mapFile;
	}
	
	/**
	 * Get the Config Directory Path
	 * @return Returns the Config Dir of the Plugin
	 */
	public static Path getConfigDir() {
		return configDir;
	}
	
	/**
	 * Load Configuration and register Commands when the Server gets started.
	 * @see ConfigurationUtils, StatsUtils
	 */
	@Listener
	public void onServer(GameStartedServerEvent e) {
		try {
			configUtils = new ConfigurationUtils(privateConfigDir.toFile()); // Set Configuration Utils
			
			 // Set Default Settings if they don't exist
			if (configUtils.getString("map") == null) configUtils.setString("map", "map");
			if (configUtils.getLocation("spawn").getBlockX() == 0) configUtils.setLocation("spawn", 0, 101, 0);
			if (configUtils.getFloat("tickrate") == 0f) configUtils.setFloat("tickrate", 10);
			if (configUtils.getFloat("spreadPlayerDistance") == 0f) configUtils.setFloat("spreadPlayerDistance", 25);
			if (configUtils.getFloat("spreadPlayerRadius") == 0f) configUtils.setFloat("spreadPlayerRadius", 130);
			if (configUtils.getString("map") == null) configUtils.setString("map", "TheNile");
			if (configUtils.getString("hitdelay") == null) configUtils.setString("hitdelay", "false");
			
			mapFile = new File(privateConfigDir.toFile(), configUtils.getString("map")); // Set Schematics File
			
			statsUtils = new StatsUtils(); // Set Stats Utils
			
			// Try to Load Stats
			try {
				statsUtils.loadStats(privateConfigDir.toFile());
			} catch (Exception nothinghappend) {
				System.out.println("Nothing happend lmao");
			}
		} catch (Exception e1) {
			System.out.println("[FFA] Couldn't load Configuration!");
		}
		
		configDir = privateConfigDir; // Make Config Public
		
		// Register Commands
		Sponge.getCommandManager().register(this, new ReadyCommand(), "ready");
		Sponge.getCommandManager().register(this, new ForceendCommand(), "forceend");
		Sponge.getCommandManager().register(this, new ForcestartCommand(), "forcestart"); 
		Sponge.getCommandManager().register(this, new ReloadmapCommand(), "reloadmap");
		Sponge.getCommandManager().register(this, new StatisticsCommand(), "statistics");
		Sponge.getCommandManager().register(this, new FFAConfigCommand(), "ffa");
		Sponge.getCommandManager().register(this, new ItemsCommand(), "items");
		Sponge.getCommandManager().register(this, new SetKitCommand(), "setkit");
		Sponge.getCommandManager().register(this, new SetItemsCommand(), "setitems");
	}
	
	/**
	 * When a Player join's add them to the current Game
	 * @see Game
	 */
	@Listener
	public void onLogin(ClientConnectionEvent.Join e) {
		e.setMessageCancelled(true); // Disable Join Message
		Game.playerJoin(e.getTargetEntity()); // Join Game
	}
	
	/**
	 * When an Item gets dropped and the game is not running cancel the drop.
	 */
	@Listener
	public void onDrop(DropItemEvent e) {
		
		// Cancel Drop Event when the game isn't running
		e.getCause().first(Player.class).ifPresent((p) -> {
			if (!Game.isRunning && !p.hasPermission("mgw.bypasslobby")) e.setCancelled(true);
		});
	}
	
	/**
	 * When the Game is not Running disable PVP.
	 * Or Update Statistics if the Game is runing and this Damage Event will kill the Player
	 * @see StatsUtils
	 */
	@Listener
	public void onPvP(DamageEntityEvent e) {
		try {
			e.getCause().first(Player.class).ifPresent((killer) -> {
				
				if (Game.isRunning && e.getTargetEntity().getType() == EntityTypes.PLAYER) {
					if (Game.team1.contains(((Player) e.getTargetEntity()).getName()) && Game.team1.contains(killer.getName())) e.setBaseDamage(0);
					else if (Game.team2.contains(((Player) e.getTargetEntity()).getName()) && Game.team2.contains(killer.getName())) e.setBaseDamage(0);
				}
				if (Game.isRunning && e.willCauseDeath() && e.getTargetEntity().getType() == EntityTypes.PLAYER) { // When a Player dies and the game is running
					
					// Try to get the Player by using Dirty Code
					for (Player p : Sponge.getServer().getOnlinePlayers()) {
						if (e.getCause().getContext().toString().contains(p.getName())) {
							FFA.statsUtils.updateStats(p.getUniqueId(), 1, 0, 0, 0); // Give the killer a Kill
							SoundsUtils.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, p);
							break; // We found him!
						}
					}
					
					SoundsUtils.playSound(SoundTypes.ENTITY_ELDER_GUARDIAN_CURSE, (Player) e.getTargetEntity());
					
					// Add one Death and one ran Game to the Player
					FFA.statsUtils.updateStats((Player) e.getTargetEntity(), 0, 1, 1, 0);
				}
			});
			Optional<DamageSource> source = e.getCause().first(DamageSource.class); // Try to get the Damage Source
			if (!Game.isRunning && source.get() != DamageSources.VOID) e.setCancelled(true); // If it's not void and the game isn't running cancel it
		} catch (Exception f) {
			//
		}
	}
	
	/**
	 * Remove the Player from the game if they disconnect
	 * @see Game
	 */
	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e) throws CommandException {
		Game.playerOut(e.getTargetEntity()); // Remove the Player from the Game
	}
	
	/**
	 * Remove the Player from the game if they die
	 * @see Game
	 */
	@Listener
	public void onDeath(DestructEntityEvent.Death e) {
		if (e.getTargetEntity().getType() == EntityTypes.PLAYER) { // If a Player died
			Game.playerOut((Player) e.getTargetEntity()); // Remove the Player from the Game
		}
	}
	
	/**
	 * When a Player is closing their inventory and the invenory is a Kit Inventory
	 * @see KitUtils
	 */
	@Listener
	public void onInv(InteractInventoryEvent.Close e) throws Exception {
		if (edit.containsKey(((Player) e.getSource()).getName())) { // If the Player is in the /setitems list
			KitUtils.saveKit(edit.get(((Player) e.getSource()).getName()), e.getTargetInventory(), privateConfigDir); // Save the Inventory to the Kit
			edit.remove(((Player) e.getSource()).getName()); // Player is no longer editing
		}
	}
	
	/**
	 * Update the Map File
	 * @see Game
	 */
	public static void setMapFile(String map) {
		mapFile = new File(configDir.toFile(), map); // Set Schematics File
	}
}
