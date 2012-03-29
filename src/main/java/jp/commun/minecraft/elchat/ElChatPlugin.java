/*
 * Copyright 2012 ayunyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.DynmapChannel;
import jp.commun.minecraft.elchat.channel.PluginChannel;
import jp.commun.minecraft.elchat.command.ChannelCommand;
import jp.commun.minecraft.elchat.command.ElChatCommand;
import jp.commun.minecraft.elchat.command.IRCCommand;
import jp.commun.minecraft.elchat.irc.IRCManager;
import jp.commun.minecraft.elchat.listener.DynmapListener;
import jp.commun.minecraft.elchat.listener.IRCListener;
import jp.commun.minecraft.elchat.listener.PlayerListener;
import jp.commun.minecraft.elchat.listener.ServerListener;
import jp.commun.minecraft.elchat.message.Message;
import jp.commun.minecraft.elchat.message.PluginMessage;
import jp.commun.minecraft.util.command.CommandManager;
import jp.commun.minecraft.util.command.CommandPermissionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

public class ElChatPlugin extends JavaPlugin implements ElChatAPI {
    private static ElChatPlugin plugin;
    private CommandManager commandManager;
    private IRCManager ircManager;
    private PlayerManager playerManager;
    private ChannelManager channelManager;
    private RomaToHiraData romaToHiraData;

    private PermissionsExAdapter permissionsExAdapter;
    private DynmapAPI dynmapAPI;

    @Override
    public void onEnable() {
        plugin = this;

        loadConfiguration();

        commandManager = new CommandManager();
        commandManager.register(new ElChatCommand(this));
        commandManager.register(new IRCCommand(this));
        commandManager.register(new ChannelCommand(this));

        channelManager = new ChannelManager(this);
        channelManager.loadConfig();

        playerManager = new PlayerManager(this);
        playerManager.loadConfig();

        romaToHiraData = new RomaToHiraData(this);
        romaToHiraData.loadConfig();

        ircManager = new IRCManager(this);
        ircManager.loadConfig();
        ircManager.connect();

        permissionsExAdapter = new PermissionsExAdapter(this);

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new ServerListener(this), this);
        pm.registerEvents(new IRCListener(this), this);

        Plugin permissionsExPlugin = pm.getPlugin("PermissionsEx");
        permissionsExAdapter.setPermissionsEx(permissionsExPlugin);

        Plugin dynmapPlugin = pm.getPlugin("dynmap");
        if (dynmapPlugin != null) {
            this.setDynmapPlugin(dynmapPlugin);
            if (dynmapAPI != null) {
                getLogger().info("dynmap detected. using: " + dynmapPlugin.getDescription().getFullName());
                pm.registerEvents(new DynmapListener(this), this);
            }
        }

        getLogger().info("ElChat enabled!");
    }

    @Override
    public void onDisable() {
        channelManager.saveConfig();
        playerManager.saveConfig();
        plugin.saveConfig();

        this.ircManager.disconnect();

        getLogger().info("ElChat disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            commandManager.execute(sender, command, args);
        } catch (CommandPermissionException e) {
            sender.sendMessage("You don't have permission to do this.");
        } catch (CommandException e) {
            sender.sendMessage(e.getMessage());
        }

        return false;
    }


    public static ElChatPlugin getPlugin() {
        return plugin;
    }

    private void setDynmapPlugin(Plugin dynmapPlugin) {
        if (dynmapPlugin instanceof DynmapAPI) {
            dynmapAPI = (DynmapAPI) dynmapPlugin;
            Channel channel = getChannelManager().getChannel("dynmap");
            if (channel == null) {
                plugin.getLogger().info("adding dynmap channel");
                channel = new DynmapChannel("dynmap");
                channel.addForward(getChannelManager().getDefaultChannel());
                getChannelManager().addChannel(channel);
            }
        } else {
            dynmapAPI = null;
        }
    }

    public DynmapAPI getDynmapAPI() {
        return dynmapAPI;
    }

    public PermissionsExAdapter getPermissionsExAdapter() {
        return permissionsExAdapter;
    }

    public void loadConfiguration() {
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public void disablePlugin() {
        this.getPluginLoader().disablePlugin(this);
    }

    public IRCManager getIRCManager() {
        return ircManager;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public RomaToHiraData getRomaToHiraData() {
        return romaToHiraData;
    }

    @Override
    public void sendMessage(String channel, String message) {
        Channel c = channelManager.getChannel(channel);
        if (c == null) {
            c = new PluginChannel(channel);
            c.addForward(channelManager.getDefaultChannel());
            channelManager.addChannel(c);
        }
        Message m = new PluginMessage(message);
        c.sendMessage(m);
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
}
