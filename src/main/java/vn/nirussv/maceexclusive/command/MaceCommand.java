package vn.nirussv.maceexclusive.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;
import vn.nirussv.maceexclusive.config.ConfigManager;
import vn.nirussv.maceexclusive.mace.MaceFactory;
import vn.nirussv.maceexclusive.mace.MaceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaceCommand implements CommandExecutor, TabCompleter {

    private final MaceExclusivePlugin plugin;
    private final MaceManager maceManager;
    private final ConfigManager configManager;
    private final MaceFactory maceFactory;

    public MaceCommand(MaceExclusivePlugin plugin, MaceManager maceManager, ConfigManager configManager, MaceFactory maceFactory) {
        this.plugin = plugin;
        this.maceManager = maceManager;
        this.configManager = configManager;
        this.maceFactory = maceFactory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "info":
                handleInfo(sender);
                break;
            case "reset":
                if (!checkPerm(sender, "mace.admin")) return true;
                handleReset(sender);
                break;
            case "give":
                if (!checkPerm(sender, "mace.admin")) return true;
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(configManager.getPrefixedMessage("only-player"));
                    return true;
                }
                handleGive(player);
                break;
            case "reload":
                if (!checkPerm(sender, "mace.admin")) return true;
                configManager.reload();
                sender.sendMessage(configManager.getPrefixedMessage("config-reloaded"));
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void handleGive(Player player) {
        // Force give mace
        if (maceManager.canCraftMace()) {
            ItemStack mace = maceFactory.createMace();
            maceManager.registerMace(mace, player.getUniqueId());
            player.getInventory().addItem(mace);
            maceManager.onPlayerBecameHolder(player, player.getLocation());
            player.sendMessage(configManager.getPrefixedMessage("mace.received"));
        } else {
             Map<String, String> placeholders = Map.of("player", String.valueOf(maceManager.getCurrentHolderName()));
             player.sendMessage(configManager.getPrefixedMessage("mace.already-exists", placeholders));
        }
    }

    private void handleReset(CommandSender sender) {
        if (maceManager.reset()) {
            sender.sendMessage(configManager.getPrefixedMessage("mace.reset"));
        } else {
            sender.sendMessage(configManager.getPrefixedMessage("mace.not-found"));
        }
    }

    private void handleInfo(CommandSender sender) {
        String holder = maceManager.getCurrentHolderName();
        if (holder != null) {
            Map<String, String> placeholders = Map.of("player", holder);
            sender.sendMessage(configManager.getPrefixedMessage("mace.holder-info", placeholders));
        } else {
            sender.sendMessage(configManager.getPrefixedMessage("mace.not-found"));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(configManager.getMessage("help.header"));
        sender.sendMessage(configManager.getMessage("help.link"));
        sender.sendMessage(Component.text("Commands: /macee <info|give|reset|reload>"));
        sender.sendMessage(configManager.getMessage("help.footer"));
    }

    private boolean checkPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(configManager.getPrefixedMessage("no-permission"));
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> subs = new ArrayList<>(List.of("info", "help"));
            if (sender.hasPermission("mace.admin")) {
                subs.add("give");
                subs.add("reset");
                subs.add("reload");
            }
            
            for (String s : subs) {
                if (s.startsWith(args[0].toLowerCase())) completions.add(s);
            }
            return completions;
        }
        return List.of();
    }
}
