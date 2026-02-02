package vn.nirussv.maceexclusive.mace;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;
import vn.nirussv.maceexclusive.config.ConfigManager;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class MaceManager {

    private final MaceExclusivePlugin plugin;
    private final MaceRepository repository;
    private final ConfigManager configManager;
    private final MaceFactory factory;
    
    private final NamespacedKey MACE_KEY;
    private final NamespacedKey MACE_OWNER_KEY;

    public MaceManager(MaceExclusivePlugin plugin, MaceRepository repository, ConfigManager configManager, MaceFactory factory) {
        this.plugin = plugin;
        this.repository = repository;
        this.configManager = configManager;
        this.factory = factory;
        
        this.MACE_KEY = new NamespacedKey(plugin, "exclusive_mace_id");
        this.MACE_OWNER_KEY = new NamespacedKey(plugin, "exclusive_mace_owner");
    }

    public boolean isRegisteredMace(ItemStack item) {
        // Must contain the specific tracking key
        if (item == null || item.getType() != Material.MACE) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(MACE_KEY, PersistentDataType.BYTE);
    }

    public boolean canCraftMace() {
        return !repository.isMaceRegistered();
    }

    public boolean registerMace(ItemStack item, UUID owner) {
        if (item == null || item.getType() != Material.MACE) return false;
        if (repository.isMaceRegistered()) return false;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        
        pdc.set(MACE_KEY, PersistentDataType.BYTE, (byte) 1);
        pdc.set(MACE_OWNER_KEY, PersistentDataType.STRING, owner.toString());
        
        item.setItemMeta(meta);
        repository.setCurrentHolder(owner);
        return true;
    }

    public void onPlayerBecameHolder(Player player, Location location) {
        repository.setCurrentHolder(player.getUniqueId());
        
        // Visual effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 10, 0, false, false, true)); // 10s glow
        player.playSound(location, org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.5f);
        
        broadcastOwnership(player, location);
        showAcquisitionUI(player);
    }

    private void broadcastOwnership(Player player, Location location) {
        Map<String, String> placeholders = Map.of(
            "player", player.getName(),
            "x", String.valueOf(location.getBlockX()),
            "y", String.valueOf(location.getBlockY()),
            "z", String.valueOf(location.getBlockZ()),
            "world", location.getWorld().getName()
        );
        
        Component msg = configManager.getMessage("mace.crafted", placeholders);
        Bukkit.broadcast(msg);
    }
    
    private void showAcquisitionUI(Player player) {
        Component title = configManager.getMessage("mace.title");
        Component subtitle = configManager.getMessage("mace.subtitle");
        
        Title titleObj = Title.title(
            title,
            subtitle,
            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );
        player.showTitle(titleObj);
        player.sendMessage(configManager.getPrefixedMessage("mace.warning"));
    }

    public boolean reset() {
        if (!repository.isMaceRegistered()) return false;
        repository.reset();
        return true;
    }
    
    public String getCurrentHolderName() {
        UUID uuid = repository.getCurrentHolder();
        if (uuid == null) return null;
        Player p = Bukkit.getPlayer(uuid);
        return (p != null) ? p.getName() : Bukkit.getOfflinePlayer(uuid).getName();
    }
}
