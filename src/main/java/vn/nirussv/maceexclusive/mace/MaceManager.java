package vn.nirussv.maceexclusive.mace;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
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

    public MaceManager(MaceExclusivePlugin plugin, MaceRepository repository, ConfigManager configManager, MaceFactory factory) {
        this.plugin = plugin;
        this.repository = repository;
        this.configManager = configManager;
        this.factory = factory;
    }

    public MaceType getMaceType(ItemStack item) {
        return factory.getMaceType(item);
    }

    public boolean isRegisteredMace(ItemStack item) {
        return getMaceType(item) != null;
    }

    public boolean isPowerMace(ItemStack item) {
        return getMaceType(item) == MaceType.POWER;
    }

    public boolean isChaosMace(ItemStack item) {
        return getMaceType(item) == MaceType.CHAOS;
    }

    public boolean canCraft(MaceType type) {
        return !repository.isRegistered(type);
    }

    public boolean register(ItemStack item, UUID owner, MaceType type) {
        if (item == null || item.getType() != Material.MACE) {
            return false;
        }
        if (repository.isRegistered(type)) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, type.getPdcKey());
        pdc.set(key, PersistentDataType.BYTE, (byte) 1);
        
        NamespacedKey ownerKey = new NamespacedKey(plugin, type.getPdcKey() + "_owner");
        pdc.set(ownerKey, PersistentDataType.STRING, owner.toString());
        
        item.setItemMeta(meta);
        repository.setHolder(type, owner);
        return true;
    }

    public void onPlayerBecameHolder(Player player, Location location, MaceType type) {
        repository.setHolder(type, player.getUniqueId());
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false, true));
        player.playSound(location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.5f);
        
        broadcastOwnership(player, location, type);
        showAcquisitionUI(player, type);
    }

    private void broadcastOwnership(Player player, Location location, MaceType type) {
        Map<String, String> placeholders = Map.of(
            "player", player.getName(),
            "x", String.valueOf(location.getBlockX()),
            "y", String.valueOf(location.getBlockY()),
            "z", String.valueOf(location.getBlockZ()),
            "world", location.getWorld().getName()
        );
        
        String messageKey = type == MaceType.CHAOS ? "chaos.crafted" : "mace.crafted";
        Component msg = configManager.getMessage(messageKey, placeholders);
        Bukkit.broadcast(msg);
    }
    
    private void showAcquisitionUI(Player player, MaceType type) {
        String titleKey = type == MaceType.CHAOS ? "chaos.title" : "mace.title";
        String subtitleKey = type == MaceType.CHAOS ? "chaos.subtitle" : "mace.subtitle";
        String warningKey = type == MaceType.CHAOS ? "chaos.warning" : "mace.warning";
        
        Component title = configManager.getMessage(titleKey);
        Component subtitle = configManager.getMessage(subtitleKey);
        
        Title titleObj = Title.title(
            title,
            subtitle,
            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );
        player.showTitle(titleObj);
        player.sendMessage(configManager.getPrefixedMessage(warningKey));
    }

    public boolean reset(MaceType type) {
        if (!repository.isRegistered(type)) {
            return false;
        }
        repository.reset(type);
        return true;
    }

    public boolean resetAll() {
        repository.resetAll();
        return true;
    }
    
    public String getHolderName(MaceType type) {
        UUID uuid = repository.getHolder(type);
        if (uuid == null) {
            return null;
        }
        Player p = Bukkit.getPlayer(uuid);
        return (p != null) ? p.getName() : Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Deprecated
    public boolean canCraftMace() {
        return canCraft(MaceType.POWER);
    }

    @Deprecated
    public boolean registerMace(ItemStack item, UUID owner) {
        return register(item, owner, MaceType.POWER);
    }

    @Deprecated
    public void onPlayerBecameHolder(Player player, Location location) {
        onPlayerBecameHolder(player, location, MaceType.POWER);
    }

    @Deprecated
    public boolean reset() {
        return reset(MaceType.POWER);
    }

    @Deprecated
    public String getCurrentHolderName() {
        return getHolderName(MaceType.POWER);
    }
}
