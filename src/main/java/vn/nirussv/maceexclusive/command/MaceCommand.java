package vn.nirussv.maceexclusive.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vn.nirussv.maceexclusive.MaceExclusivePlugin;
import vn.nirussv.maceexclusive.config.ConfigManager;
import vn.nirussv.maceexclusive.mace.MaceFactory;
import vn.nirussv.maceexclusive.mace.MaceManager;
import vn.nirussv.maceexclusive.mace.MaceType;

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
            case "info" -> handleInfo(sender, args);
            case "reset" -> {
                if (!checkPerm(sender, "mace.admin")) return true;
                handleReset(sender, args);
            }
            case "give" -> {
                if (!checkPerm(sender, "mace.admin")) return true;
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(configManager.getPrefixedMessage("only-player"));
                    return true;
                }
                handleGive(player, args);
            }
            case "reload" -> {
                if (!checkPerm(sender, "mace.admin")) return true;
                configManager.reload();
                sender.sendMessage(configManager.getPrefixedMessage("config-reloaded"));
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void handleGive(Player player, String[] args) {
        MaceType type = parseType(args, 1);
        
        if (maceManager.canCraft(type)) {
            ItemStack mace = maceFactory.createMace(type);
            maceManager.register(mace, player.getUniqueId(), type);
            player.getInventory().addItem(mace);
            maceManager.onPlayerBecameHolder(player, player.getLocation(), type);
            
            String msgKey = type == MaceType.CHAOS ? "chaos.received" : "mace.received";
            player.sendMessage(configManager.getPrefixedMessage(msgKey));
        } else {
            String holderName = maceManager.getHolderName(type);
            Map<String, String> placeholders = Map.of("player", holderName != null ? holderName : "Unknown");
            String msgKey = type == MaceType.CHAOS ? "chaos.already-exists" : "mace.already-exists";
            player.sendMessage(configManager.getPrefixedMessage(msgKey, placeholders));
        }
    }

    private void handleReset(CommandSender sender, String[] args) {
        MaceType type = parseType(args, 1);
        
        if (maceManager.reset(type)) {
            String msgKey = type == MaceType.CHAOS ? "chaos.reset" : "mace.reset";
            sender.sendMessage(configManager.getPrefixedMessage(msgKey));
        } else {
            String msgKey = type == MaceType.CHAOS ? "chaos.not-found" : "mace.not-found";
            sender.sendMessage(configManager.getPrefixedMessage(msgKey));
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        MaceType type = parseType(args, 1);
        String holder = maceManager.getHolderName(type);
        
        if (holder != null) {
            Map<String, String> placeholders = Map.of("player", holder);
            String msgKey = type == MaceType.CHAOS ? "chaos.holder-info" : "mace.holder-info";
            sender.sendMessage(configManager.getPrefixedMessage(msgKey, placeholders));
        } else {
            String msgKey = type == MaceType.CHAOS ? "chaos.not-found" : "mace.not-found";
            sender.sendMessage(configManager.getPrefixedMessage(msgKey));
        }
    }

    private MaceType parseType(String[] args, int index) {
        if (args.length > index) {
            String typeArg = args[index].toLowerCase();
            if (typeArg.equals("chaos")) {
                return MaceType.CHAOS;
            }
        }
        return MaceType.POWER;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(configManager.getMessage("help.header"));
        sender.sendMessage(configManager.getMessage("help.link"));
        sender.sendMessage(Component.text("Commands: /macee <info|give|reset|reload> [power|chaos]"));
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
                if (s.startsWith(args[0].toLowerCase())) {
                    completions.add(s);
                }
            }
            return completions;
        }
        
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("info") || sub.equals("give") || sub.equals("reset")) {
                List<String> types = new ArrayList<>();
                for (MaceType type : MaceType.values()) {
                    String name = type.name().toLowerCase();
                    if (name.startsWith(args[1].toLowerCase())) {
                        types.add(name);
                    }
                }
                return types;
            }
        }
        
        return List.of();
    }
}
