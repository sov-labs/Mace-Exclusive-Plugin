package vn.nirussv.maceexclusive.mace;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MaceRepository {

    private final MaceExclusivePlugin plugin;
    private final File dataFile;
    private FileConfiguration config;
    private final Map<MaceType, UUID> holders = new EnumMap<>(MaceType.class);

    public MaceRepository(MaceExclusivePlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "mace-data.yml");
        load();
    }

    private void load() {
        if (!dataFile.exists()) {
            return;
        }
        
        config = YamlConfiguration.loadConfiguration(dataFile);
        
        for (MaceType type : MaceType.values()) {
            String holderString = config.getString(type.name().toLowerCase() + ".holder");
            if (holderString != null && !holderString.isEmpty()) {
                try {
                    holders.put(type, UUID.fromString(holderString));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void save() {
        if (config == null) {
            config = new YamlConfiguration();
        }
        
        for (MaceType type : MaceType.values()) {
            String path = type.name().toLowerCase();
            UUID holder = holders.get(type);
            config.set(path + ".registered", holder != null);
            config.set(path + ".holder", holder != null ? holder.toString() : null);
        }
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save mace data", e);
        }
    }

    public boolean isRegistered(MaceType type) {
        return holders.containsKey(type) && holders.get(type) != null;
    }

    public UUID getHolder(MaceType type) {
        return holders.get(type);
    }

    public void setHolder(MaceType type, UUID holder) {
        if (holder != null) {
            holders.put(type, holder);
        } else {
            holders.remove(type);
        }
        save();
    }

    public void reset(MaceType type) {
        holders.remove(type);
        save();
    }

    public void resetAll() {
        holders.clear();
        save();
    }

    @Deprecated
    public boolean isMaceRegistered() {
        return isRegistered(MaceType.POWER);
    }

    @Deprecated
    public UUID getCurrentHolder() {
        return getHolder(MaceType.POWER);
    }

    @Deprecated
    public void setCurrentHolder(UUID holder) {
        setHolder(MaceType.POWER, holder);
    }

    @Deprecated
    public void reset() {
        reset(MaceType.POWER);
    }
}
