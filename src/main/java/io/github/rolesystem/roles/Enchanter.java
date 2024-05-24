package io.github.rolesystem.roles;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Enchanter implements Listener {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;
    private final String currentRole = "enchanter";

    public Enchanter(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    private List<Material> BLOCKS = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        boolean isConnected = playerRole.isRoleConnected(currentRole);
        boolean isOp = player.isOp();
        boolean isCreative = false;

        if (player.getGameMode() == GameMode.CREATIVE) isCreative = true;

        // If the player is not an Enchanter AND not in Creative gamemode AND not an operator, then...
        if (!(playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isCreative && isOp))) {

            if(isConnected) {

                BLOCKS = playerRole.getMaterialList(currentRole, "usable-blocks");

                // If player tries to open enchant table or brewing stand, cancel event
                if(clickedBlock != null && BLOCKS.contains(clickedBlock.getType())) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }
}
