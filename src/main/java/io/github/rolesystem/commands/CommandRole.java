package io.github.rolesystem.commands;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRole implements CommandExecutor {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;

    public CommandRole(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String role = playerRole.getPlayerRole(player);

                player.sendMessage("§aYou are a §6" + role);
                plugin.getLogger().info("[LOG] " + player.getName() + " checked his own role");
            } else {
                sender.sendMessage("§cPlease precise which player you want to see the role");
            }

        } else if (playerRole.roleExist(args[0]) || args[0].equals("list")) {
            sender.sendMessage(playerRole.getList(args[0]));
        } else {
            Player player = Bukkit.getPlayerExact(args[0]);

            if(player == null) {
                sender.sendMessage("§cThis player does not exist");
            } else {
                String role = playerRole.getPlayerRole(player);
                sender.sendMessage("§a" + player.getName() + " is a §6" + role);
                plugin.getLogger().info("[LOG] " + sender.getName() + " checked " + player.getName() + " role");
            }
        }

        return true;
    }
}
