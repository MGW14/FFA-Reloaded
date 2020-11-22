package work.mgnet.commands;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import work.mgnet.FFA;
import work.mgnet.utils.StatsUtils.Stats;

public class StatisticsCommand implements CommandCallable {

	/**
	 * Runned when the Command gets called
	 */
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		Player player = (Player) source; // Cast Player
		if (arguments.length() != 0) {
			// Try to get Player
			try {
				if (Sponge.getServer().getPlayer(arguments).get() == null) throw new Exception();
				player = Sponge.getServer().getPlayer(arguments).get();
			} catch (Exception e) {
				source.sendMessage(Text.of("§b» §7That Player is not online!"));
			}
		}
		// Try to show the Stats
		try {
			Stats stats = FFA.statsUtils.getStats(player.getUniqueId());
			source.sendMessage(Text.of("§b» §7Showing Stats of " + player.getName()));
			source.sendMessage(Text.of("§b» §7Kills: §b" + stats.kills));
			source.sendMessage(Text.of("§b» §7Deaths: §b" + stats.deaths));
			source.sendMessage(Text.of("§b» §7K/D: §b" + Math.round((double) stats.kills / ((stats.deaths == 0) ? 1 : stats.deaths))));
			source.sendMessage(Text.of("§b» §7Games played: §b" + stats.games));
			source.sendMessage(Text.of("§b» §7Games won: §b" + stats.gamesWon));
			source.sendMessage(Text.of("§b» §7Win chance: §b" + ((((double) stats.gamesWon) / ((double) stats.games)) * 100) + "%"));
		} catch (ArithmeticException e1) {
			source.sendMessage(Text.of("§b» §7Win chance: Not enough data!"));
		} catch (Exception e) {
			source.sendMessage(Text.of("§b» §7Couldn't show stats!"));
		}
		return CommandResult.builder().successCount(1).build();
	}

	// Stuff noone cares about
	
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		return null;
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return true;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return null;
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return null;
	}

	@Override
	public Text getUsage(CommandSource source) {
		return null;
	}
	
}
