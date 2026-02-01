package vn.nirussv.maceexclusive.mace;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class MaceRepository {

    private final MaceExclusivePlugin plugin;
    private final File dataFile;
    private FileConfiguration config;
    
    private boolean maceRegistered;
    private UUID currentHolder;

    public MaceRepository(MaceExclusivePlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "mace-data.yml");
        load();
    }

    private void load() {
        if (!dataFile.exists()) {
            maceRegistered = false;
            currentHolder = null;
            return;
        }
        
        config = YamlConfiguration.loadConfiguration(dataFile);
        maceRegistered = config.getBoolean("registered", false);
        
        String holderString = config.getString("holder");
        if (holderString != null && !holderString.isEmpty()) {
            try {
                currentHolder = UUID.fromString(holderString);
            } catch (IllegalArgumentException e) {
                currentHolder = null;
            }
        } else {
            currentHolder = null;
        }
    }

    public void save() {
        if (config == null) {
            config = new YamlConfiguration();
        }
        
        config.set("registered", maceRegistered);
        config.set("holder", currentHolder != null ? currentHolder.toString() : null);
        
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save mace data", e);
        }
    }

    public boolean isMaceRegistered() {
        return maceRegistered;
    }

    public void setMaceRegistered(boolean registered) {
        this.maceRegistered = registered;
        if (!registered) {
            this.currentHolder = null;
        }
        save();
    }

    public UUID getCurrentHolder() {
        return currentHolder;
    }

    public void setCurrentHolder(UUID holder) {
        this.currentHolder = holder;
        this.maceRegistered = holder != null;
        save();
    }

    public void reset() {
        this.maceRegistered = false;
        this.currentHolder = null;
        save();
    }
}
