package at.haha007.commandmessages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CommandMessages extends JavaPlugin {
    private final List<String> cmds = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        FileConfiguration cfg = getConfig();
        for (String cmd : cfg.getKeys(false)) {
            register(cmd, miniMessage.deserialize(Objects.requireNonNull(cfg.getString(cmd))));
        }
        Objects.requireNonNull(getCommand("cmdmsg")).setExecutor((sender, command, label, args) -> {
            reload();
            sender.sendMessage(ChatColor.GOLD + "CommandMessages config reloaded");
            return true;
        });
    }

    private void reload() {
        onDisable();
        onEnable();
    }

    private void register(String command, Component message) {
        cmds.add(command);
        Bukkit.getCommandMap().register(command, new Command(command) {
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                sender.sendMessage(message);
                return true;
            }
        });
    }

    public void onDisable() {
        for (String cmd : cmds) {
            getServer().getCommandMap().getKnownCommands().remove(cmd);
        }
        cmds.clear();
    }
}
