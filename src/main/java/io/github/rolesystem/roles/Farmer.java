package io.github.rolesystem.roles;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Farmer implements Listener {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;
    private final String currentRole = "farmer";

    public Farmer(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(this.plugin);
    }

    private final List<Material> FARMBLOCKS = Arrays.asList(
            Material.FARMLAND,
            Material.GRASS_BLOCK,
            Material.DIRT_PATH,
            Material.COARSE_DIRT,
            Material.ROOTED_DIRT,
            Material.DIRT
    );

    private final List<Material> FARMITEMS = Arrays.asList(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
    );

    private final List<Material> SEEDS = Arrays.asList(
            Material.WHEAT_SEEDS,
            Material.PUMPKIN_SEEDS,
            Material.MELON_SEEDS,
            Material.BEETROOT_SEEDS,
            Material.TORCHFLOWER_SEEDS,
            Material.POTATO,
            Material.CARROT
    );

    private final List<Material> GROWING_SEEDS = Arrays.asList(
            Material.PUMPKIN,
            Material.MELON,
            Material.WHEAT,
            Material.PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.BEETROOTS,
            Material.TORCHFLOWER_CROP,
            Material.POTATOES,
            Material.CARROTS
    );

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        boolean isConnected = playerRole.isRoleConnected(currentRole);
        boolean isOp = player.isOp();
        boolean isCreative = false;

        // True if player's gamemode is Creative
        if (player.getGameMode() == GameMode.CREATIVE) isCreative = true;

        // If the player is not a Farmer AND not in Creative gamemode AND not an operator, then...
        if (!(playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isCreative && isOp))) {

            if(isConnected) {
                Block clickedBlock = event.getClickedBlock();
                Material itemType;

                // Prevent NullPointerException
                if (event.getItem() != null) itemType = event.getItem().getType();
                else itemType = null;

                // Prevent the player from planting seeds
                if(clickedBlock != null && clickedBlock.getType() == Material.FARMLAND) {
                    if(SEEDS.contains(itemType))  {
                        event.setCancelled(true);
                        playerRole.sendErrorMessage(player, currentRole);
                    }
                }

                // Prevent player from destroying seeds by jumping on them
                if(clickedBlock != null && event.getAction() == Action.PHYSICAL) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }

                // Prevent player from harvest dirt blocks
                if (clickedBlock != null && FARMBLOCKS.contains(clickedBlock.getType())) {
                    if (FARMITEMS.contains(itemType)) {
                        event.setCancelled(true);
                        playerRole.sendErrorMessage(player, currentRole);
                    }

                    // Also prevent him from planting cocoa beans
                } else if (itemType == Material.COCOA_BEANS) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }

    /*
    Checks if player is trying to break a block with growing seeds planted on it
    Or
    If  player tries to break growing seeds (does work if player tries to jump on seeds)
    */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Material blockAboveMaterial = brokenBlock.getRelative(BlockFace.UP).getType();

        Player player = event.getPlayer();

        boolean isConnected = playerRole.isRoleConnected(currentRole);
        boolean isOp = player.isOp();
        boolean isCreative;

        // True if player's gamemode is Creative
        if (player.getGameMode() == GameMode.CREATIVE) isCreative = true;
        else isCreative = false;

        // If the player is not a Farmer AND not in Creative gamemode AND not an operator, then...
        if (!(playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isCreative && isOp))) {

            if(isConnected) {
                // Prevent him from destroying seeds / block with seeds planted on top
                if (GROWING_SEEDS.contains(blockAboveMaterial) || GROWING_SEEDS.contains(brokenBlock.getType())) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }
}
