package io.github.rolesystem.roles;

import io.github.rolesystem.playerdata.PlayerRole;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Smith implements Listener {
    private final JavaPlugin plugin;
    private final PlayerRole playerRole;
    private final String currentRole = "smith";
    public Smith(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerRole = new PlayerRole(plugin);
    }

    private List<Material> ITEMS = new ArrayList<>();

    @EventHandler
    private void onPlayerCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack result = event.getRecipe().getResult();

        boolean isConnected = playerRole.isRoleConnected(currentRole);
        boolean isOp = player.isOp();
        boolean isCreative = false;

        if(player.getGameMode() == GameMode.CREATIVE) isCreative = true;

        // If player role isn't Smith OR player isn't op and in creative gamemode, then...
        if(!(playerRole.getPlayerRole(player).equalsIgnoreCase(currentRole) || (isOp && isCreative))) {

            if(isConnected) {

                ITEMS = playerRole.getMaterialList(currentRole, "craftable-items");

                // Cancel action if player is trying to craft any kind of armor
                if(result != null && ITEMS.contains(result.getType())) {
                    event.setCancelled(true);
                    playerRole.sendErrorMessage(player, currentRole);
                }
            }
        }
    }
}
