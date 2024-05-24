package io.github.rolesystem;

import io.github.rolesystem.commands.CommandRolesReset;
import io.github.rolesystem.commands.CommandRole;
import io.github.rolesystem.commands.CommandSetRole;
import io.github.rolesystem.playerdata.PlayerRole;
import io.github.rolesystem.playerdata.RolesList;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class RoleSystem extends JavaPlugin {
    private final RolesList rolesList = new RolesList(this);
    private final PlayerRole playerRole = new PlayerRole(this);
    FileConfiguration config = this.getConfig();

    // When the server starts/reloads, execute the following instructions
    @Override
    public void onEnable() {
        playerRole.checkCSVFile();

        // Data
        getServer().getPluginManager().registerEvents(new PlayerRole(this), this);

        // Config
        saveDefaultConfig();

        // Misc
        getLogger().info("Thank you for running Role System :)");
        rolesList.updateCSVFile();

        // Roles
        List<String> ROLES = rolesList.getRoleList();

        // Start listener for every enabled role
        for(String role : ROLES) {
            try {
                Class<?> clazz = Class.forName("io.github.rolesystem.roles." + role.substring(0, 1).toUpperCase() + role.substring(1));
                Object instance = clazz.getDeclaredConstructor(JavaPlugin.class).newInstance(this);

                getServer().getPluginManager().registerEvents((Listener) instance, this);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // Commands
        getCommand("setrole").setExecutor(new CommandSetRole(this));
        getCommand("role").setExecutor(new CommandRole(this));
        getCommand("rolesreset").setExecutor(new CommandRolesReset(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("See you soon!");
    }
}
