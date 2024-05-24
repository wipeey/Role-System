package io.github.rolesystem.roles;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Miner implements Listener {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;
    private final String currentRole = "miner";

    public Miner(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    private List<Material> ORES = new ArrayList<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        boolean isConnected = playerRole.isRoleConnected(currentRole);
        boolean isOp = player.isOp();
        boolean isCreative = false;

        if (player.getGameMode() == GameMode.CREATIVE) isCreative = true;

        // Check if player is a miner OR if he is OP and in creative gamemode
        if (!(playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isCreative && isOp))) {

            if(isConnected) {

                ORES = playerRole.getMaterialList(currentRole, "mineable-blocks");

                // Cancel action if player tries to break an ore
                if(ORES.contains(block.getType())) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }
}
