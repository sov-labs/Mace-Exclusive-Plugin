package vn.nirussv.maceexclusive;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import vn.nirussv.maceexclusive.command.MaceCommand;
import vn.nirussv.maceexclusive.config.ConfigManager;
import vn.nirussv.maceexclusive.listener.MaceListener;
import vn.nirussv.maceexclusive.mace.MaceFactory;
import vn.nirussv.maceexclusive.mace.MaceManager;
import vn.nirussv.maceexclusive.mace.MaceRepository;

import java.util.List;
import java.util.Map;

public class MaceExclusivePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MaceFactory maceFactory;
    private MaceRepository maceRepository;
    private MaceManager maceManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        // Initialize Core Components
        this.configManager = new ConfigManager(this);
        this.maceFactory = new MaceFactory(this);
        this.maceRepository = new MaceRepository(this);
        this.maceManager = new MaceManager(this, maceRepository, configManager, maceFactory);
        
        // Register Command
        MaceCommand cmd = new MaceCommand(this, maceManager, configManager, maceFactory);
        getCommand("macee").setExecutor(cmd);
        getCommand("macee").setTabCompleter(cmd);
        
        // Register Listener
        getServer().getPluginManager().registerEvents(new MaceListener(this, maceManager, configManager), this);
        getServer().getPluginManager().registerEvents(new vn.nirussv.maceexclusive.listener.EffectMaceListener(this, maceManager, configManager), this);
        
        // Start Effect Task (Passive Effects)
        new vn.nirussv.maceexclusive.task.MaceEffectTask(this, maceManager).runTaskTimer(this, 10L, 5L);
        
        // Register Recipe
        registerRecipe();
        
        getLogger().info("Mace-Exclusive has been enabled! Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        if (maceRepository != null) {
            maceRepository.save();
        }
    }

    private void registerRecipe() {
        if (!getConfig().getBoolean("mace.recipe.enabled", true)) return;

        NamespacedKey requestKey = new NamespacedKey(this, "exclusive_mace_recipe");
        ItemStack result = maceFactory.createMace();
        ShapedRecipe recipe = new ShapedRecipe(requestKey, result);

        List<String> shape = getConfig().getStringList("mace.recipe.shape");
        if (shape.size() != 3) {
            getLogger().warning("Invalid recipe shape in config! parsing default.");
            recipe.shape(" H ", " I ", " B ");
            recipe.setIngredient('H', Material.HEAVY_CORE);
            recipe.setIngredient('I', Material.NETHERITE_INGOT);
            recipe.setIngredient('B', Material.BREEZE_ROD);
        } else {
            recipe.shape(shape.toArray(new String[0]));
            
            ConfigurationSection ingredients = getConfig().getConfigurationSection("mace.recipe.ingredients");
            if (ingredients != null) {
                for (String key : ingredients.getKeys(false)) {
                    String matName = ingredients.getString(key);
                    Material mat = Material.matchMaterial(matName);
                    if (mat != null) {
                        recipe.setIngredient(key.charAt(0), mat);
                    } else {
                        getLogger().warning("Invalid ingredient material: " + matName);
                    }
                }
            }
        }

        getServer().addRecipe(recipe);
    }
}
