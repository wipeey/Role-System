package io.github.rolesystem.commands;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandSetRole implements CommandExecutor {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;

    public CommandSetRole(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 1) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String role = args[0];

                if(playerRole.roleExist(role)) {
                    playerRole.playerAssignRole(player, role);
                    plugin.getLogger().info(player.getName() + " has set " + player.getName() + " role to " + role);
                } else {
                    sender.sendMessage("§cThis role does not exist");
                }

                return true;
            } else {
                return false;
            }

        } else if(args.length >= 2) {
            Player player = Bukkit.getPlayerExact(args[0]);
            String role = args[1];

            if(player == null) {
                sender.sendMessage("§cThis player could not be found");
            } else if (playerRole.roleExist(role)){
                playerRole.playerAssignRole(player, role);
                sender.sendMessage("§2Successfully set " + player.getName() + "'s role to §6" + role);
                Bukkit.broadcastMessage("§a" + sender.getName() + " has set " + player.getName() + " role to §6" + role);
            } else {
                sender.sendMessage("§cThis role does not exist");
            }

            return true;
        }

        return false;
    }
}
