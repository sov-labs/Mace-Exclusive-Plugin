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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.inventory.Recipe;

public class MaceExclusivePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MaceFactory maceFactory;
    private MaceRepository maceRepository;
    private MaceManager maceManager;

    @Override

    public void onEnable() {
        try {
            // 1. Config
            saveDefaultConfig();
            reloadConfig(); // Ensure loaded
            
            // 2. Managers
            this.configManager = new ConfigManager(this);
            // Verify config loaded 
            if (!getConfig().contains("settings.language")) {
                 getLogger().warning("Config.yml might be corrupt or missing settings! regenerating defaults if empty...");
            }
            this.configManager.reload(); // Load lang files immediately
            
            this.maceFactory = new MaceFactory(this);
            this.maceRepository = new MaceRepository(this);
            this.maceManager = new MaceManager(this, maceRepository, configManager, maceFactory);
            
            // 3. Command
            MaceCommand cmd = new MaceCommand(this, maceManager, configManager, maceFactory);
            if (getCommand("macee") != null) {
                getCommand("macee").setExecutor(cmd);
                getCommand("macee").setTabCompleter(cmd);
            } else {
                getLogger().severe("Command 'macee' not found in plugin.yml!");
            }
            
            // 4. Listeners
            getServer().getPluginManager().registerEvents(new MaceListener(this, maceManager, configManager), this);
            getServer().getPluginManager().registerEvents(new vn.nirussv.maceexclusive.listener.EffectMaceListener(this, maceManager, configManager), this);
            getServer().getPluginManager().registerEvents(new vn.nirussv.maceexclusive.listener.ChaosMaceListener(this, maceManager, configManager), this);
            
            // 5. Tasks
            try {
                new vn.nirussv.maceexclusive.task.MaceEffectTask(this, maceManager).runTaskTimer(this, 10L, 5L);
            } catch (Exception e) {
                 getLogger().log(Level.SEVERE, "Failed to start MaceEffectTask", e);
            }
            
            // 6. Recipes
            removeVanillaRecipe();
            registerRecipe();
            
            getLogger().info("Mace-Exclusive has been enabled! Version: " + getDescription().getVersion());
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "CRITICAL ERROR: Failed to enable Mace-Exclusive!", t);
            // Do not disable plugin immediately to allow reading logs, but functionality will be broken.
        }
    }

    @Override
    public void onDisable() {
        if (maceRepository != null) {
            maceRepository.save();
        }
    }

    private void removeVanillaRecipe() {
        Iterator<Recipe> it = getServer().recipeIterator();
        while (it.hasNext()) {
            Recipe r = it.next();
            if (r.getResult().getType() == Material.MACE) {
                if (r instanceof ShapedRecipe sr) {
                    if (sr.getKey().getNamespace().equals("minecraft")) {
                        it.remove();
                        getLogger().info("Removed vanilla Mace recipe.");
                    }
                }
            }
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
                        getLogger().warning("Invalid ingredient material definition: " + key + " -> " + matName);
                        getLogger().warning("Invalid ingredient material: " + matName);
                    }
                }
            }
        }
        
        // Debug
        getLogger().info("Registered custom Mace recipe with key: " + requestKey);

        getServer().addRecipe(recipe);
        
        if (getConfig().getBoolean("mace-chaos.enabled", true)) {
             registerChaosRecipe();
        }
    }

    private void registerChaosRecipe() {
        NamespacedKey requestKey = new NamespacedKey(this, "chaos_mace_recipe");
        ItemStack result = maceFactory.createChaosMace();
        ShapedRecipe recipe = new ShapedRecipe(requestKey, result);
        
        List<String> shape = getConfig().getStringList("mace-chaos.recipe.shape");
        if (shape.size() != 3) {
             recipe.shape("NHN", "HMH", "NWN");
             recipe.setIngredient('N', Material.NETHERITE_INGOT);
             recipe.setIngredient('H', Material.HEAVY_CORE);
             recipe.setIngredient('M', Material.MACE);
             recipe.setIngredient('W', Material.WITHER_ROSE);
        } else {
             recipe.shape(shape.toArray(new String[0]));
             ConfigurationSection ingredients = getConfig().getConfigurationSection("mace-chaos.recipe.ingredients");
             if (ingredients != null) {
                 for (String key : ingredients.getKeys(false)) {
                      String matName = ingredients.getString(key);
                      Material mat = Material.matchMaterial(matName);
                      if (mat != null) {
                          recipe.setIngredient(key.charAt(0), mat);
                      }
                 }
             }
        }
        
        getServer().addRecipe(recipe);
    }
}
