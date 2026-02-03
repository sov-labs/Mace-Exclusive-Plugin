package vn.nirussv.maceexclusive.mace;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;

import java.util.ArrayList;
import java.util.List;

public class MaceFactory {

    private final MaceExclusivePlugin plugin;

    public MaceFactory(MaceExclusivePlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack createMace(MaceType type) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(type.getConfigPath());
        if (section == null) {
            return new ItemStack(Material.MACE);
        }

        String matName = section.getString("material", "MACE");
        Material material = Material.matchMaterial(matName);
        if (material == null) {
            material = Material.MACE;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            String name = section.getString("name");
            if (name != null) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
            }

            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
                List<Component> componentLore = new ArrayList<>();
                for (String line : lore) {
                    componentLore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
                }
                meta.lore(componentLore);
            }

            if (section.contains("custom-model-data")) {
                meta.setCustomModelData(section.getInt("custom-model-data"));
            }

            NamespacedKey key = new NamespacedKey(plugin, type.getPdcKey());
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            
            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack createPowerMace() {
        return createMace(MaceType.POWER);
    }
    
    public ItemStack createChaosMace() {
        return createMace(MaceType.CHAOS);
    }

    public MaceType getMaceType(ItemStack item) {
        if (item == null || item.getType() != Material.MACE) {
            return null;
        }
        if (!item.hasItemMeta()) {
            return null;
        }
        
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        
        for (MaceType type : MaceType.values()) {
            NamespacedKey key = new NamespacedKey(plugin, type.getPdcKey());
            if (pdc.has(key, PersistentDataType.BYTE)) {
                return type;
            }
        }
        
        return null;
    }

    public boolean isMaceItem(ItemStack item) {
        return getMaceType(item) != null;
    }

    public boolean isPowerMace(ItemStack item) {
        return getMaceType(item) == MaceType.POWER;
    }

    public boolean isChaosMace(ItemStack item) {
        return getMaceType(item) == MaceType.CHAOS;
    }
}
