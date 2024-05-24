package io.github.rolesystem.commands;


import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class CommandRolesReset implements CommandExecutor {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;
    private boolean confirm = false;

    public CommandRolesReset(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        List<String> uuids = new ArrayList<>();

        if(args.length == 0) {
            uuids.add(player.getUniqueId().toString());
            sender.sendMessage("§6Type /rolesreset confirm to confirm this action");
        } else if (args[0].equalsIgnoreCase("confirm")) {
            if(uuids.contains(player.getUniqueId().toString())) {
                playerRole.resetList();
                plugin.getLogger().info("Players role list was reset by " + player.getName());
                player.sendMessage("§2Players roles were successfully reset");
                confirm = false;
            } else {
                sender.sendMessage("§cYou must type /rolesreset first");
            }
        }

        return true;
    }
}
