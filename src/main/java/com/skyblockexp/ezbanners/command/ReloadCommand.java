package com.skyblockexp.ezbanners.command;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    private final EzBannersPlugin plugin;

    public ReloadCommand(EzBannersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ezbanners.reload")) {
            sender.sendMessage("§cYou do not have permission to reload EzBanners.");
            return true;
        }
        plugin.reloadEzBannersConfig();
        sender.sendMessage("§aEzBanners configuration reloaded.");
        return true;
    }
}
