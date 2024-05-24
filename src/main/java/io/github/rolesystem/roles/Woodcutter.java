package io.github.rolesystem.roles;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Woodcutter implements Listener {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;
    private final String currentRole = "woodcutter";
    public Woodcutter(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    private List<Material> LOGS = new ArrayList<>();
    private List<Material> CRAFT = new ArrayList<>();

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

                LOGS = playerRole.getMaterialList(currentRole, "mineable-blocks");

                // Cancel action if player tries to break a wooden log
                if(LOGS.contains(block.getType())) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack result = event.getRecipe().getResult();

        boolean isConnected = playerRole.isRoleConnected(currentRole);
        boolean isOp = player.isOp();
        boolean isCreative = false;

        if (player.getGameMode() == GameMode.CREATIVE) isCreative = true;

        // Check if player is a miner OR if he is OP and in creative gamemode
        if(!playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isCreative && isOp)) {

            if(isConnected) {

                CRAFT = playerRole.getMaterialList(currentRole, "craftable-items");

                // Cancel action if player tries to craft an illegal item
                if(result != null && CRAFT.contains(result.getType())) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }
}