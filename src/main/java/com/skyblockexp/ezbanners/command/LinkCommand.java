package com.skyblockexp.ezbanners.command;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LinkCommand implements CommandExecutor {
    private final EzBannersPlugin plugin;

    public LinkCommand(EzBannersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ezbanners.link")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 2 || !args[0].equalsIgnoreCase("link")) {
            sender.sendMessage(ChatColor.RED + "Usage: /ezbanners link <token>");
            return true;
        }
        String token = args[1].trim();
        if (token.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Token cannot be empty.");
            return true;
        }
        plugin.getConfig().set("api.token", token);
        plugin.saveConfig();
        plugin.reloadEzBannersConfig();
        sender.sendMessage(ChatColor.GREEN + "EzBanners token updated. Sync will resume shortly.");
        return true;
    }
}
