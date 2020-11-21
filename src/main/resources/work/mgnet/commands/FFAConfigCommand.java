package work.mgnet.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import work.mgnet.FFA;
import work.mgnet.utils.CommandUtils;


public class FFAConfigCommand implements CommandCallable{

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if (!source.hasPermission("mgw.admin")) return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
		
		String[] args=arguments.split(" ");
		
		if(args[0].equalsIgnoreCase("pvp")) {
			try {
				FFA.configUtils.setLocation("pvp", Double.parseDouble((args[1])), Double.parseDouble((args[2])), Double.parseDouble((args[3])));
			}catch(NumberFormatException e) {
				throw new CommandException(Text.of("Couldn't parse coordinates"));
			} catch (ObjectMappingException e) {
				throw new CommandException(Text.of("Some unknown mapping error occured"));
			}
		}else if(args[0].equalsIgnoreCase("spawn")) {
			try {
				FFA.configUtils.setLocation("spawn", Double.parseDouble((args[1])), Double.parseDouble((args[2])), Double.parseDouble((args[3])));
			}catch(NumberFormatException e) {
				throw new CommandException(Text.of("Couldn't parse coordinates"));
			} catch (ObjectMappingException e) {
				throw new CommandException(Text.of("Some unknown mapping error occured"));
			}
		}else if(args[0].equalsIgnoreCase("tickrate")) {
			try {
				FFA.configUtils.setFloat("tickrate", Float.parseFloat(args[1]));
			} catch (NumberFormatException e) {
				throw new CommandException(Text.of("Couldn't parse tickrate"));
			} catch (ObjectMappingException e) {
				throw new CommandException(Text.of("Some unknown mapping error occured"));
			}
		}else if(args[0].equalsIgnoreCase("spreadPlayerRadius")){
			try {
				FFA.configUtils.setFloat("spreadPlayerRadius", Float.parseFloat(args[1]));
			} catch (NumberFormatException e) {
				throw new CommandException(Text.of("Couldn't parse radius"));
			} catch (ObjectMappingException e) {
				throw new CommandException(Text.of("Some unknown mapping error occured"));
			}
		}else if(args[0].equalsIgnoreCase("spreadPlayerDistance")) {
			try {
				FFA.configUtils.setFloat("spreadPlayerDistance", Float.parseFloat(args[1]));
			} catch (NumberFormatException e) {
				throw new CommandException(Text.of("Couldn't parse distance"));
			} catch (ObjectMappingException e) {
				throw new CommandException(Text.of("Some unknown mapping error occured"));
			}
		}else if(args[0].equalsIgnoreCase("map")) {
			try {
				FFA.configUtils.setString("map", args[1]);
				FFA.setMapFile();
			} catch (ObjectMappingException e) {
				throw new CommandException(Text.of("Some unknown mapping error occured"));
			}
		}
		else {
			source.sendMessage(Text.of("§b» §7/ffa pvp | spawn | tickrate | spreadPlayerRadius | spreadPlayerDistance | map"));
			return CommandResult.builder().successCount(1).build();
		}
		FFA.configUtils.reloadConfiguration();
		source.sendMessage(Text.of("§b» §7Successfully changed config option "+args[0]));
		return CommandResult.builder().successCount(1).affectedEntities(Sponge.getGame().getServer().getOnlinePlayers().size()).build();
	}
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition)
			throws CommandException {
		Collection<String> liste= new ArrayList<String>();
		String[] args=arguments.split(" ");
		if(args.length==1) {
			liste.add("pvp");
			liste.add("spawn");
			liste.add("tickrate");
			liste.add("spreadPlayerRadius");
			liste.add("spreadPlayerDistance");
		}
		return CommandUtils.getListOfStringsMatchingLastWord(args, liste);
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return source.hasPermission("mgw.edit");
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.of(Text.of("Changes configuration"));
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.of(Text.of("No help is available atm, please ask your questions after the beep.... *BEEP*"));
	}

	@Override
	public Text getUsage(CommandSource source) {
		return Text.of("/ffa <configname> <value>");
	}
}
