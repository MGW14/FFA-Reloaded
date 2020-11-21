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
import work.mgnet.utils.KitUtils;

public class SetKitCommand implements CommandCallable {
	
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if (!source.hasPermission("mgw.admin")) return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		if (arguments.isEmpty()) {
			source.sendMessage(Text.of("户7 You need to specify the Kit"));
			return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		}
		try {
			KitUtils.inves.clear();
			((Player) source).openInventory(KitUtils.loadKit(arguments.toLowerCase(), FFA.getConfigDir()));
			for (Player p : Sponge.getServer().getOnlinePlayers()) {
				p.getInventory().clear();
				p.sendMessage(Text.of("户7 A new Kit has been selected"));
				if (p.getOpenInventory() != null) p.closeInventory();
			}
			FFA.selectedKit = arguments.toLowerCase();
		} catch (Exception e) {
			source.sendMessage(Text.of("户c That Kit doesn't exist!"));
			/*source.sendMessage(Text.of("户7 Creating a new Kit"));
			FFA.selectedKit = arguments.toLowerCase();
			try {
				KitUtils.saveKit(arguments.toLowerCase(), Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(Sponge.getPluginManager().getPlugin("ffa")), FFA.getConfigDir());
			} catch (Exception e1) {
				source.sendMessage(Text.of("户c Something went very wrong..."));
			}*/
		}
		return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
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
