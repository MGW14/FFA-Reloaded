package work.mgnet;

import java.util.ArrayList;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import work.mgnet.utils.CommandUtils;
import work.mgnet.utils.KitUtils;

public class Game {

	public static ArrayList<String> players = new ArrayList<String>(); // List of playing Players
	public static boolean isRunning = false; // Is the Game Running
	
	
	/**
	 * Starts the Game by setting a bunch of settings like tickrate difficulty etc..
	 * @see CommandUtils
	 */
	public static void startGame() {
		isRunning = true; // Make it Running
		
		CommandUtils.runCommand("tickrate " + FFA.configUtils.getFloat("tickrate")); // Change Tickrate
		CommandUtils.runCommand("difficulty 1"); // Change Difficulty
		CommandUtils.runCommand("effect @a clear"); // Clear Effects
		
		// Load Vars
		Location<World> pvpLocation = FFA.configUtils.getLocation("pvp");
		double spreadPlayerDistance = FFA.configUtils.getFloat("spreadPlayerDistance"); 
		double spreadPlayerRadius = FFA.configUtils.getFloat("spreadPlayerRadius");
		
		CommandUtils.runCommand("spreadplayers " + pvpLocation.getBlockX() + " " + pvpLocation.getBlockZ() + " "+spreadPlayerDistance+" " + spreadPlayerRadius + " false @a"); // Spread the Players around the map
		
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) { // Every PLayer
			player.offer(Keys.GAME_MODE, GameModes.SURVIVAL); // Survival Mode
			player.offer(Keys.HEALTH, 20D); // Full HP
			player.offer(Keys.FOOD_LEVEL, 20); // Full Hunger
			player.offer(Keys.EXPERIENCE_LEVEL, 0); // No Levels
			player.sendMessage(Text.of("§b»§7 The Game has begun. Kill everyone to win")); // Send Message
		}
	}
	
	/**
	 * Add a Player to the Game
	 * @param Player that will join the Game
	 */
	public static void playerJoin(Player p) {
		p.getInventory().clear(); // Clear their Inventory
		p.setLocation(FFA.configUtils.getLocation("spawn")); // Teleport them to Spawn
		CommandUtils.runCommand("spawnpoint " + p.getName()); // Set their respawn Point to Spawn
		if (isRunning) { // If the Game is already Running
			p.offer(Keys.GAME_MODE, GameModes.SPECTATOR); // Set them to Spectator
			p.sendMessage(Text.of("§b»§7 A game is already running, after the round you will participate")); // Send them a Message
			CommandUtils.runCommand("tickrate " + FFA.configUtils.getDouble("tickrate") + " " + p.getName()); // Change their Tickrate
		} else {
			p.offer(Keys.GAME_MODE, GameModes.ADVENTURE); // Set their GameMode to Adventure
			p.sendMessage(Text.of("§b»§7 Type §a/items §7to see all the items you can get. When you are ready, type §a/ready§7.")); // Send them the Message
			CommandUtils.runCommand("tickrate 20 " + p.getName()); // Set their tickrate back to t20
		}
	}
	
	/**
	 * Remove a Player from the Game.
	 * @param Player to remove
	 */
	public static void playerOut(Player p) {
		if (!players.contains(p.getName())) return; // If they aren't in the Game quit
		players.remove(p.getName()); // Remove them from the Game
		p.offer(Keys.GAME_MODE, GameModes.SPECTATOR); // Set them to Spectator
		if (players.size() == 1) { // If one Player remains
			Player winner = Sponge.getServer().getPlayer(players.get(0)).get(); // Get the Winner
			for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
				player.sendTitle(Title.of(Text.of(winner.getName() + " won!"))); // Let everyone know!
			}
			FFA.statsUtils.updateStats(winner, 0, 0, 1, 1); // Give them a game and a win
			endGame(); // End The Game
		}
	}
	
	/**
	 * End the Game
	 */
	public static void endGame() {
		if (!isRunning) return; // Cannot end the Game if it isn't running
		players.clear(); // Clear the Players
		KitUtils.inves.clear(); // Reset the Inventories
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) { // Every Player
			player.setLocation(FFA.configUtils.getLocation("spawn")); // Tp to Spawn
			player.getInventory().clear(); // Clear Inv
			player.offer(Keys.HEALTH, 20D); // Set HP
			player.offer(Keys.FOOD_LEVEL, 20); // Set Food
			player.offer(Keys.EXPERIENCE_LEVEL, 0); // Set XP
			
			// Send Messages
			player.sendMessage(Text.of("§b»§e The Game has ended"));
			player.sendMessage(Text.of("§b»§7 Type §a/items §7to see all the items you can get. When you are ready, type §a/ready§7."));
			
			// Set back to Adventure
			player.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
		}
		
		// Reset Stuff
		CommandUtils.runCommand("difficulty 0");
		CommandUtils.runCommand("effect @a clear");
		CommandUtils.runCommand("tickrate 20");
		CommandUtils.runCommand("kill @e[type=!player]");
	
		isRunning = false; // Set Game not running
	}
	
}
