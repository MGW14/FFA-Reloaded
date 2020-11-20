package work.mgnet.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;

public class CommandUtils {

	public static void runCommand(String command) {
		String cmd = command.split(" ")[0];
		String parameters = command.split(" ", 2)[1];
		try {
			Sponge.getCommandManager().get(cmd).get().getCallable().process(Sponge.getServer().getConsole(), parameters);
		} catch (CommandException e) {
			System.out.println("[CommandUtils] Error Couldn't run Command: " + e.getClass().getName());
		}
	}
	
}
