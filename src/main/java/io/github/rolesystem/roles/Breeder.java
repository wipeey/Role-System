package io.github.rolesystem.roles;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Breeder implements Listener {
    private final JavaPlugin plugin;

    private final PlayerRole playerRole;

    public Breeder(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    private final String currentRole = "breeder";
    private final List<Material> SEEDS = Arrays.asList(
            Material.WHEAT,
            Material.BAMBOO,
            Material.WHEAT_SEEDS,
            Material.BEETROOT_SEEDS,
            Material.CARROT,
            Material.GOLDEN_CARROT,
            Material.SWEET_BERRIES,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.BONE,
            Material.APPLE,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM
    );

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        Entity entity = event.getEntity();

        if(target instanceof Player) {
            Player player = (Player) target;

            boolean isConnected = playerRole.isRoleConnected(currentRole);
            boolean isOp = player.isOp();
            boolean isCreative = false;
            if (player.getGameMode() == GameMode.CREATIVE) isCreative = true;

            // If player is not a breeder OR not in creative and OP
            if (!(playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isCreative && isOp))) {

                if(isConnected) {
                    ItemStack item = player.getItemInHand();
                    Material itemInHand = item.getType();

                    // Make player unable to attract animals
                    if (itemInHand != null && SEEDS.contains(itemInHand) && entity instanceof Animals) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)  {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        boolean isConnected = playerRole.isRoleConnected(currentRole);
        boolean isOp = player.isOp();
        boolean isCreative = false;
        if (player.getGameMode() == GameMode.CREATIVE) isCreative = true;

        // If player is not a breeder OR not in creative and OP
        if (!(playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isCreative && isOp))) {

            if(isConnected) {
                ItemStack item = event.getPlayer().getItemInHand();
                Material itemInHand = item.getType();

                // Prevent him from feeding animals
                if(itemInHand != null && SEEDS.contains(itemInHand) && entity instanceof Animals) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }
}
