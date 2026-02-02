package vn.nirussv.maceexclusive.mace;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;

import java.util.ArrayList;
import java.util.List;

public class MaceFactory {

    private final MaceExclusivePlugin plugin;
    public static final String MACE_KEY_STRING = "mace_exclusive_item";

    public MaceFactory(MaceExclusivePlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack createMace() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("mace");
        if (section == null) return new ItemStack(Material.MACE);

        String matName = section.getString("material", "MACE");
        Material material = Material.matchMaterial(matName);
        if (material == null) material = Material.MACE;

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

            NamespacedKey key = new NamespacedKey(plugin, MACE_KEY_STRING);
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            item.setItemMeta(meta);
        }

        return item;
    }
    
    public boolean isMaceItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;
        NamespacedKey key = new NamespacedKey(plugin, MACE_KEY_STRING);
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
