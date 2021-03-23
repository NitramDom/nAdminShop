package adminshop.commands;

import adminshop.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandHandler implements CommandExecutor {
    private static HashMap<String, CommandInterface> commands = new HashMap<String, CommandInterface>();

    public void register(String name, CommandInterface cmd) {
        commands.put(name, cmd);
    }

    public boolean exists(String name) {
        return commands.containsKey(name);
    }

    public CommandInterface getExecutor(String name) {
        return commands.get(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (commandLabel.equals("shop")) {
                if (args.length <= 0) {
                    getExecutor("openShop").onCommand(sender, cmd, commandLabel, args);
                }

                //return invalidCommand(player, commandLabel);

                return true;
            } else {
                return invalidCommand(player, commandLabel);
            }
        } else {
            sender.sendMessage(Main.prefixError + "Vous devez être un joueur pour utiliser cette commande.");
            return true;
        }
    }

    public boolean invalidCommand(Player player, String commandLabel) {
        player.sendMessage(Main.prefixError + "Cette commande est invalide, utilisez /" + commandLabel + " help.");
        return true;
    }

}