package vn.nirussv.maceexclusive.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;
import vn.nirussv.maceexclusive.config.ConfigManager;
import vn.nirussv.maceexclusive.mace.MaceManager;
import vn.nirussv.maceexclusive.task.InventoryShuffleTask;

import java.util.Random;

public class ChaosMaceListener implements Listener {

    private final MaceExclusivePlugin plugin;
    private final MaceManager maceManager;
    private final ConfigManager configManager;
    private final Random random = new Random();

    public ChaosMaceListener(MaceExclusivePlugin plugin, MaceManager maceManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.maceManager = maceManager;
        this.configManager = configManager;
    }

    // IdentifyChaosMace logic could be in MaceManager, but for now we check lore/model or a specific PersistentData if we added it.
    // For simplicity, we'll check if it's a mace and has specific model data/lore from config.
    private boolean isChaosMace(ItemStack item) {
        if (item == null || item.getType() != Material.MACE) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasCustomModelData()) return false;
        
        int chaosModel = plugin.getConfig().getInt("mace-chaos.custom-model-data", 2002);
        return item.getItemMeta().getCustomModelData() == chaosModel;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        // Validation handled by custom recipe registration, but double check if needed
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftChaos(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (!isChaosMace(result)) return;

        if (event.getWhoClicked() instanceof Player player) {
            applySelfCurse(player);
            announceChaos(player, "crafted");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickupChaos(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack item = event.getItem().getItemStack();
        
        if (isChaosMace(item)) {
            // Check if player already "owns" it or just verify it's a chaos mace pickup event
            // The requirement says "cũng áp dụng nếu có player nào đó lụm mace này" (also apply if someone picks it up)
            // To avoid spamming effects every time they drop/pickup, we might want a cooldown, but request implies "when picked up".
            // We'll apply it.
            // Check if player has cooldown tag? For now, just apply.
            applySelfCurse(player);
            announceChaos(player, "found");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCombat(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (isChaosMace(weapon)) {
            // 20% Shuffle
            if (random.nextDouble() < plugin.getConfig().getDouble("mace-chaos.effects.shuffle-inventory.chance", 0.2)) {
                int duration = plugin.getConfig().getInt("mace-chaos.effects.shuffle-inventory.duration", 5);
                int interval = plugin.getConfig().getInt("mace-chaos.effects.shuffle-inventory.interval", 5);
                
                victim.sendMessage("§c§k||| §r§cSYSTEM ERROR: INVENTORY CORRUPTION §c§k|||");
                new InventoryShuffleTask(victim, duration, interval).runTaskTimer(plugin, 0L, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            ItemStack weapon = killer.getInventory().getItemInMainHand();
            if (isChaosMace(weapon)) {
                if (plugin.getConfig().getBoolean("mace-chaos.effects.glitch-kill-name", true)) {
                    Component originalMsg = event.deathMessage();
                    if (originalMsg != null) {
                         // Simple replace string logic or rebuild component
                         // Rebuilding: Victim was slain by @#%@#%
                         event.deathMessage(
                                 Component.text(victim.getName(), NamedTextColor.RED)
                                 .append(Component.text(" was OBLITERATED by ", NamedTextColor.GRAY))
                                 .append(Component.text("§kERROR_404", NamedTextColor.DARK_PURPLE))
                         );
                    }
                }
            }
        }
    }

    private void applySelfCurse(Player player) {
        if (!plugin.getConfig().getBoolean("mace-chaos.effects.self-curse.enabled", true)) return;
        
        int witherDur = plugin.getConfig().getInt("mace-chaos.effects.self-curse.wither-duration", 10) * 20;
        int shuffleDur = plugin.getConfig().getInt("mace-chaos.effects.self-curse.shuffle-duration", 10);
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherDur, 1));
        
        player.sendMessage("§5The §kCHAOS§r§5 corrupts your body!");
        new InventoryShuffleTask(player, shuffleDur, 10).runTaskTimer(plugin, 0L, 1L);
    }
    
    private void announceChaos(Player player, String action) {
        if (!plugin.getConfig().getBoolean("mace-chaos.effects.announce-on-chat", true)) return;
        
        String msgKey = action.equals("crafted") ? "mace.chaos-crafted" : "mace.chaos-found";
        // Since we don't have these keys in lang file yet, we use hardcoded strictly or fallback
        // Better to use broadcast
        
        Component msg = Component.text("Warning: ", NamedTextColor.RED)
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" has obtained the ", NamedTextColor.GRAY))
                .append(Component.text("MACE OF CHAOS", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD, TextDecoration.OBFUSCATED))
                .append(Component.text("!", NamedTextColor.RED));
                
        Bukkit.broadcast(msg);
        
        if (plugin.getConfig().getBoolean("mace-chaos.effects.glowing", true)) {
             player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0)); // 30s glow
        }
    }
}
