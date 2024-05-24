package io.github.rolesystem.playerdata;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRole implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final RolesList assignRole;
    private final String fileName = "player_roles.csv";
    private final File player_roles;
    private final File roles_list;

    public PlayerRole(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.assignRole = new RolesList(plugin);
        this.player_roles = new File(plugin.getDataFolder(), fileName);
        this.roles_list = new File(plugin.getDataFolder(), "roles_list.csv");
    }

    // Send an error message when player performs illegal action
    public void sendErrorMessage(Player player, String role) {
        String errormsg = "§cError";

        errormsg = "§cYou are not a " + role;

        // Checks for config
        if(config.getBoolean("messages.errorRole")) {
            player.sendMessage(errormsg);
        }
    }

    // Check if the CSV file already exists - if not, creates it
    public void checkCSVFile() {
        try {
            if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
                plugin.getLogger().info("Failed to create plugin directory");
                return;
            }

            if (player_roles.createNewFile()) {
                plugin.getLogger().info("CSV file created");
            } else {
                plugin.getLogger().info("CSV file already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    @param role The role to analyze
    @return True if the role is in roles_list.csv
     */
    public boolean roleExist(String role) {
        try (BufferedReader br = new BufferedReader(new FileReader(roles_list))) {
            String line;

            while((line = br.readLine()) != null) {
                String[] rolesList = line.split(",");

                if(role.equalsIgnoreCase(rolesList[0])) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
    @aram role The role to analyze
    @return True if a player is connected and has the specified role
     */
    public boolean isRoleConnected(String role) {
        // Return true if player with this role is connected
        for(Player player : Bukkit.getOnlinePlayers()) {
            if (getPlayerRole(player).equalsIgnoreCase(role)) {
                return true;
            }
        }

        // If config is set to do not requireConnected, it always returns true
        // (allows to bypass the role limitation feature)
        if(!config.getBoolean("roles.requireConnected")) {
            return true;
        }

        return false;
    }

    /*
    @param uuid The UUID to check
    @return True if the uuid is registered in player_roles.csv
     */
    public boolean isUUIDRegistered(String uuid) {
        try (BufferedReader br = new BufferedReader(new FileReader(player_roles))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] playerRole = line.split(",");
                if (uuid.equals(playerRole[0])) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Register a new player's UUID to player_roles.csv
    public void registerPlayer(Player player) {
        try (FileWriter fw = new FileWriter(player_roles, true)) {
            fw.write("\n" + player.getUniqueId() + ",Unassigned");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    @param player Player to get the role from
    @return The player's role
     */
    public String getPlayerRole(Player player) {
        String role = "Unfetchable";

        try (BufferedReader br = new BufferedReader(new FileReader(player_roles))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] playerRole = line.split(",");
                if (player.getUniqueId().toString().equals(playerRole[0])) {
                    role = playerRole[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return role;
    }

    /*
    @param role The role to get the list of
    @return The list of connected players with this specific role
    NOTE: If role is null, then it gets the list of all the roles connected
     */
    public String getList(String role) {
        String list = "";
        int amount = 0;

        if(role.equals("list")) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                try(BufferedReader br = new BufferedReader(new FileReader(player_roles))) {
                    String line = "";

                    while((line = br.readLine()) != null) {
                        String[] player_roles = line.split(",");

                        if(player.getUniqueId().toString().equals(player_roles[0])) {
                            list += "§6[" + getPlayerRole(player) + "]" + " §f" + player.getName() + ", ";
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if(!roleExist(role)) {
                list = "§cThis role does not exist";
                return list;
            } else {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(player_roles))) {
                        String line = "";

                        while ((line = br.readLine()) != null) {
                            String[] player_roles = line.split(",");

                            if (player.getUniqueId().toString().equals(player_roles[0]) && player_roles[1].equalsIgnoreCase(role)) {
                                amount++;
                                list += "§6" + getPlayerRole(player) + " §f" + player.getName() + ", ";
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(!list.isEmpty()) list += "\n";
                list += "§aThere are §6" + amount + " §a" + role + "s connected";
            }
        }

        return list;
    }

    // Assign a specific role to the player in player_roles.csv
    public void playerAssignRole(Player player, String role) {
        role = role.toLowerCase();

        try (BufferedReader br = new BufferedReader(new FileReader(player_roles))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                String[] playerRoles = line.split(",");
                if (playerRoles[0].equals(player.getUniqueId().toString())) {
                    line = player.getUniqueId() + "," + role;
                }
                if (!line.trim().isEmpty()) {
                    stringBuilder.append(line).append(System.lineSeparator());
                }
            }

            try (FileWriter fw = new FileWriter(player_roles)) {
                fw.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assignRole.updateCSVFile();
        player.sendMessage("§aYou have been assigned the role §6" + role);
        plugin.getLogger().info(player.getName() + " was assigned the role " + role);
    }

    // Remove a player from player_roles.csv
    public void removePlayer(Player player) {
        String uuid = player.getUniqueId().toString();

        try (BufferedReader br = new BufferedReader(new FileReader(player_roles))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                String[] playerRoles = line.split(",");

                if (playerRoles[0].equals(uuid)) {}

                else if (!line.trim().isEmpty()) {
                    stringBuilder.append(line).append(System.lineSeparator());
                }
            }

            try (FileWriter fw = new FileWriter(player_roles)) {
                fw.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assignRole.updateCSVFile();
        plugin.getLogger().info(player.getName() + " was removed for players roles list");
    }

    // Reset player_roles.csv content
    public void resetList() {

        try(FileWriter fw = new FileWriter(player_roles))  {
            fw.write("");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
    @param role The role to fetch the config from
    @param category The config's category we want to read data from
    @return The list of materials in config
     */
    public List<Material> getMaterialList(String role, String category) {
        List<Material> LIST = new ArrayList<>();
        String path = "roles." + role;

        ConfigurationSection config = plugin.getConfig().getConfigurationSection(path);

        if(config.getStringList(category) != null) {
            for(String minableBlock : config.getStringList(category)) {
                //LIST.clear();
                Material material = Material.getMaterial(minableBlock.toUpperCase());
                if(material != null) {
                    LIST.add(material);
                } else {
                    plugin.getLogger().info("Invalid material name for " + path + "." + category  + ": " + minableBlock);
                }
            }
        }

        return LIST;
    }

    // Perform actions whenever a player joins the server
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Adds user UUID & unassigned role to player_roles
        if (!isUUIDRegistered(player.getUniqueId().toString())) {
            registerPlayer(player);
        }

        // update/create roles_list.csv
        assignRole.updateCSVFile();

        // Assign a role to the player if he doesn't already have one or if his is disabled
        if (!assignRole.getRoleList().contains(getPlayerRole(player))) {
            playerAssignRole(player, assignRole.lowestAmountRole());
        }

        // Send a message informing player of his role if turned true in the config
        if(config.getBoolean("messages.welcomeRole")) {
            player.sendMessage("§aYou are a §6" + getPlayerRole(player));
        }
    }

    // Remove player from players roles list if whenever he gets banned
    @EventHandler
    public void onPlayerBanned(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Checks for server config
        if(player.isBanned() && config.getBoolean("roles.removeIfBanned")) {
            removePlayer(player);
        }
    }
}
