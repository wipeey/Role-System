package io.github.rolesystem.playerdata;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RolesList {
    private final JavaPlugin plugin;

    public RolesList(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<String> ROLES = Arrays.asList(
            "woodcutter",
            "farmer",
            "miner",
            "breeder",
            "smith",
            "enchanter"
    );

    private final List<String> INIT_ROLES = new ArrayList<>();

    // Return integer: amount of players with selected role
    public int getRoleAmount(String role) {
        File file = new File(plugin.getDataFolder(), "player_roles.csv");
        int amount = 0;
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] players_list = line.split(",");

                if (players_list.length == 2 && players_list[1].equalsIgnoreCase(role)) {
                    amount++;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return amount;
    }

    // Get the Role with the lowest amount of players
    public String lowestAmountRole() {
        int minAmount = Integer.MAX_VALUE;
        String role = "";

        for (String roleName : INIT_ROLES) {
            int roleAmount = getRoleAmount(roleName);
            if (roleAmount < minAmount) {
                minAmount = roleAmount;
                role = roleName;
            }
        }

        return role;
    }

    // Remove jobs if they are set to false in config
    public void initializeList() {
        FileConfiguration config = plugin.getConfig();

        INIT_ROLES.clear();

        for(String role : ROLES) {
            String path = "roles." + role + ".enabled";
            if(config.getBoolean(path)) {
                INIT_ROLES.add(role);
            }
        }
    }

    // Get available roles
    public List getRoleList() {
        return INIT_ROLES;
    }

    // Update roles_list.csv with correct jobs and amounts for each
    public void updateCSVFile() {
        File file = new File(plugin.getDataFolder(), "roles_list.csv");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        initializeList();

        try(FileWriter fw = new FileWriter(file, false)) {
            for(String role : INIT_ROLES) {
                fw.write(role + "," + getRoleAmount(role));
                fw.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
