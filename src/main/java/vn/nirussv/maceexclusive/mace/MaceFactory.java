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
        return createMaceFromConfig("mace", MACE_KEY_STRING);
    }
    
    public ItemStack createChaosMace() {
        String chaosKey = "mace_chaos_item"; // Different persistent key or verify by functionality
        // But for simplicity request just says "chaos mace", let's use a key if needed or model data.
        // ChaosMaceListener checks model data primarily.
        // BUT, strict mode might block it if we don't register it in MaceManager? 
        // We really should treat it as a "Mace" so strict mode applies too?
        // Request: "cả 2 cây mace đều thêm tính năng có thể tắt nhưng mặc định thông báo là có" -> "Both maces".
        // If strict mode prevents drop, does it apply to chaos mace?
        // "fix tính năng Strict mace" ... "với không chặn người chơi..."
        // Use separate key for Chaos Mace to identify it uniquely for chaos effects, 
        // but if MaceManager checks "isRegisteredMace", we might want it to return true for both?
        // MaceManager.isRegisteredMace checks MACE_KEY_STRING.
        // If we want Strict Mode to apply to Chaos Mace (likely yes, it's a mace), we should probably tag it with the same key OR MaceManager should check both.
        // For safety, let's tag it with MACE_KEY_STRING too, or rely on model data.
        // Adding a second key for chaos ID.
        return createMaceFromConfig("mace-chaos", "mace_chaos_item");
    }

    private ItemStack createMaceFromConfig(String path, String keyString) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
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

            // Tag as Mace for general purposes (if we want strict mode to apply)
            // But if we use different keys, we need to update MaceManager.
            // Let's use the specific key provided. 
            // NOTE: If keyString is different from MACE_KEY_STRING, MaceManager.isRegisteredMace will return false.
            // Strict Mode relies on MaceManager.isRegisteredMace.
            // If we want Strict Mode on Chaos Mace, we should add MACE_KEY_STRING too.
            
            NamespacedKey key = new NamespacedKey(plugin, keyString);
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            
            // Also tag as "Basic Mace" if we want Strict Mode checks?
            // Actually, let's update MaceManager to check effectively.
            
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
