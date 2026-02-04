package com.skyblockexp.ezbanners;

import com.skyblockexp.ezbanners.command.LinkCommand;
import com.skyblockexp.ezbanners.command.StatusCommand;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.api.EzBannersApi;
import com.skyblockexp.ezbanners.lifecycle.PluginLifecycle;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class EzBannersPlugin extends JavaPlugin {
    private static EzBannersPlugin instance;
    private PluginLifecycle lifecycle;

    @Override
    public void onEnable() {
        instance = this;
        lifecycle = new PluginLifecycle(this);
        lifecycle.startup();
        registerCommands();
    }

    @Override
    public void onDisable() {
        if (lifecycle != null) {
            lifecycle.shutdown();
        }
        instance = null;
        getLogger().info("[EzBanners] Disabled cleanly.");
    }

    private void registerCommands() {
        PluginCommand command = getCommand("ezbanners");
        if (command != null) {
            command.setExecutor((sender, cmd, label, args) -> {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (!sender.hasPermission("ezbanners.reload")) {
                            sender.sendMessage("§cYou do not have permission to reload EzBanners.");
                            return true;
                        }
                        lifecycle.reload();
                        sender.sendMessage("§aEzBanners configuration reloaded.");
                        return true;
                    } else if (args[0].equalsIgnoreCase("status")) {
                        return new StatusCommand(this).onCommand(sender, cmd, label, args);
                    }
                }
                // Default to link command
                return new LinkCommand(this).onCommand(sender, cmd, label, args);
            });
        } else {
            getLogger().warning("[EzBanners] Command not registered. Check plugin.yml.");
        }
    }

    public static EzBannersPlugin getInstance() {
        return instance;
    }

    public PluginLifecycle getLifecycle() {
        return lifecycle;
    }

    public EzBannersConfig getEzBannersConfig() {
        return lifecycle != null ? lifecycle.getConfig() : null;
    }

    public EzBannersApi getApi() {
        return lifecycle != null ? lifecycle.getApi() : null;
    }

    public void reloadEzBannersConfig() {
        if (lifecycle != null) {
            lifecycle.reload();
        }
    }

    public void debug(String message) {
        EzBannersConfig config = getEzBannersConfig();
        if (config != null && config.isDebugEnabled()) {
            getLogger().log(Level.INFO, "[EzBanners][Debug] " + message);
        }
    }
}
