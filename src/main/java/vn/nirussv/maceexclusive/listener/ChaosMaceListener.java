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

    private boolean isChaosMace(ItemStack item) {
        if (item == null || item.getType() != Material.MACE) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasCustomModelData()) return false;
        
        int chaosModel = plugin.getConfig().getInt("mace-chaos.custom-model-data", 2002);
        return item.getItemMeta().getCustomModelData() == chaosModel;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
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
        
        Component msg = Component.text("Warning: ", NamedTextColor.RED)
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" has obtained the ", NamedTextColor.GRAY))
                .append(Component.text("MACE OF CHAOS", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD, TextDecoration.OBFUSCATED))
                .append(Component.text("!", NamedTextColor.RED));
                
        Bukkit.broadcast(msg);
        
        if (plugin.getConfig().getBoolean("mace-chaos.effects.glowing", true)) {
             player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0));
        }
    }
}
