package vn.nirussv.maceexclusive.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;
import vn.nirussv.maceexclusive.config.ConfigManager;
import vn.nirussv.maceexclusive.mace.MaceManager;

public class MaceListener implements Listener {

    private final MaceExclusivePlugin plugin;
    private final MaceManager maceManager;
    private final ConfigManager configManager;

    public MaceListener(MaceExclusivePlugin plugin, MaceManager maceManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.maceManager = maceManager;
        this.configManager = configManager;
    }

    // Combat Logic
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        
        if (maceManager.isRegisteredMace(weapon)) {
            // Apply Blindness check (removed Tier 5 check, replaced with simple logic or permission)
            // User requested "same mechanism", implying the power.
            // We'll apply it always for the exclusive mace or check permission
            
            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0)); // 3s
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_WARDEN_ATTACK_IMPACT, 1f, 1f);
        }
    }

    // Register on Right Click if somehow obtained unregistered
    @EventHandler
    public void onMaceInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != Material.MACE) return;
        
        // If it's a mace but not registered, and none registered..
        if (!maceManager.isRegisteredMace(item)) {
            if (maceManager.registerMace(item, player.getUniqueId())) {
                maceManager.onPlayerBecameHolder(player, player.getLocation());
            }
        }
    }

    // Crafting Check - Prevent outcome if mace exists
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        ItemStack result = event.getInventory().getResult();
        
        // This checks if the OUTPUT is a MACE.
        // We assume only our recipe produces this specific mace? 
        // Or if it's vanilla mace blocking?
        // Prompt says "tách tính năng only mace". Usually this implies preventing ANY mace if it's meant to be unique,
        // OR preventing OUR mace recipe.
        // Assuming we block our mace recipe result if one exists.
        
        if (result == null || result.getType() != Material.MACE) return;

        // If a mace is already registered, block crafting
        if (!maceManager.canCraftMace()) {
            event.getInventory().setResult(null);
        }
    }

    // Handle Crafting
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftMace(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result.getType() != Material.MACE) return;

        // If registered, cancel
        if (!maceManager.canCraftMace()) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                player.sendMessage(configManager.getPrefixedMessage("mace.already-exists", 
                    java.util.Map.of("player", maceManager.getCurrentHolderName())));
            }
            return;
        }

        // Handle first craft registration
        if (event.getWhoClicked() instanceof Player player) {
            // Logic to register the mace
            // We use runTaskLater to catch the item in inventory
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                // Simplified search for the new mace
                // Try cursor or inventory
                ItemStack cursor = player.getItemOnCursor();
                if (cursor != null && cursor.getType() == Material.MACE && !maceManager.isRegisteredMace(cursor)) {
                     if (maceManager.registerMace(cursor, player.getUniqueId())) {
                         maceManager.onPlayerBecameHolder(player, player.getLocation());
                     }
                     return;
                }
                
                for (ItemStack item : player.getInventory().getContents()) {
                     if (item != null && item.getType() == Material.MACE && !maceManager.isRegisteredMace(item)) {
                         if (maceManager.registerMace(item, player.getUniqueId())) {
                             maceManager.onPlayerBecameHolder(player, player.getLocation());
                         }
                         break;
                     }
                }
            }, 1L);
        }
    }

    // Pickup
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickupMace(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack item = event.getItem().getItemStack();
        
        if (maceManager.isRegisteredMace(item)) {
            maceManager.onPlayerBecameHolder(player, player.getLocation());
        }
    }

    // Drop Prevention (Strict Mode or Config)
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        
        if (maceManager.isRegisteredMace(item)) {
            if (!configManager.isDropAllowed()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(configManager.getPrefixedMessage("mace.cannot-drop"));
            }
        }
    }

    // Inventory Move Prevention (Strict Mode)
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
         if (!(event.getWhoClicked() instanceof Player player)) return;
         if (!configManager.isStrictMode()) return;

         ItemStack current = event.getCurrentItem();
         ItemStack cursor = event.getCursor();
         
         boolean hasMace = maceManager.isRegisteredMace(current) || maceManager.isRegisteredMace(cursor);
         if (!hasMace) return;

         InventoryType top = event.getView().getTopInventory().getType();
         if (top != InventoryType.CRAFTING && event.getClickedInventory() != event.getView().getBottomInventory()) {
             event.setCancelled(true);
             player.sendMessage(configManager.getPrefixedMessage("mace.cannot-move"));
         }
    }
}
