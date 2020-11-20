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

import work.mgnet.Game;

public class ReadyCommand implements CommandCallable {

	@Override
	public CommandResult process(CommandSource src, String arguments) throws CommandException {
		if (Sponge.getGame().getServer().getOnlinePlayers().size()==1) {
			for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) player.sendMessage(Text.of("§b»§c At least 2 players are required"));
			return CommandResult.builder().successCount(1).build();
		}
		if (Game.players.contains(src.getName()) || Game.isRunning==true) return CommandResult.builder().successCount(1).build();
		Game.players.add(src.getName());
		
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) player.sendMessage(Text.of("§b»§a " + src.getName() + "§7 is now ready!"));
		
		if (Game.players.size() == Sponge.getGame().getServer().getOnlinePlayers().size()) {	
			Game.startGame();
		}
		return CommandResult.builder().successCount(1).build();
	}

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
