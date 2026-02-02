package vn.nirussv.maceexclusive.task;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;
import vn.nirussv.maceexclusive.mace.MaceManager;

public class MaceEffectTask extends BukkitRunnable {

    private final MaceExclusivePlugin plugin;
    private final MaceManager maceManager;

    public MaceEffectTask(MaceExclusivePlugin plugin, MaceManager maceManager) {
        this.plugin = plugin;
        this.maceManager = maceManager;
    }

    @Override
    public void run() {
        boolean glow = plugin.getConfig().getBoolean("effects.holding.glowing", false);
        boolean particles = plugin.getConfig().getBoolean("effects.holding.soul-particles", false);

        if (!glow && !particles) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (maceManager.isRegisteredMace(hand)) {
                if (glow) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0, false, false));
                }
                if (particles) {
                    player.getWorld().spawnParticle(Particle.SOUL, player.getLocation(), 5, 0.3, 0.1, 0.3, 0.05);
                }
            }
        }
    }
}
