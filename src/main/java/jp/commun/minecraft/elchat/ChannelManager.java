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

import jp.commun.minecraft.elchat.channel.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChannelManager {
    private final ElChatPlugin plugin;
    private final File configFile;
    private FileConfiguration config;
    private String defaultChannel;
    private Map<String, Channel> channels;

    public ChannelManager(ElChatPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "channels.yml");
        channels = new HashMap<String, Channel>();
    }

    public void loadConfig() {
        config = new YamlConfiguration();
        if (configFile.exists()) {
            try {
                this.config.load(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            config.setDefaults(YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/channels.yml")));
            config.options().copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (config.contains("default-channel")) {
            defaultChannel = config.getString("default-channel");
        }

        if (config.contains("channels")) {
            for (String name : config.getConfigurationSection("channels").getKeys(false)) {
                Channel channel;
                ConfigurationSection section = config.getConfigurationSection("channels." + name);
                String type = section.getString("type", "");

                if (channels.containsKey(name)) {
                    channel = channels.get(name);
                } else if (type.equals("dynmap")) {
                    channel = new DynmapChannel(name);
                } else if (type.equals("plugin")) {
                    channel = new PluginChannel(name);
                } else {
                    channel = new GameChannel(name);
                }
                channel.loadConfig(section);
                addChannel(channel);
            }
        }

        if (defaultChannel == null || getDefaultChannel() == null && channels.size() > 0) {
            defaultChannel = channels.keySet().iterator().next();
        }
    }

    public void reloadConfig() {
        loadConfig();
        saveConfig();
    }

    public void saveConfig() {
        config.set("channels", null);
        for (String s : channels.keySet()) {
            Channel channel = channels.get(s);
            if (channel instanceof IRCChannel) continue;
            ConfigurationSection section = config.createSection("channels." + channel.getName());
            channel.saveConfig(section);
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addChannel(Channel channel) {
        if (!channels.containsKey(channel.getName().toLowerCase())) {
            plugin.getLogger().info("ChannelManager addChannel:" + channel.getName());
            channels.put(channel.getName().toLowerCase(), channel);
        }
    }

    public void removeChannel(Channel channel) {
        if (channels.containsKey(channel.getName().toLowerCase())) {
            channels.remove(channel.getName().toLowerCase());
        }
    }

    public Channel getChannel(String name) {
        if (!channels.containsKey(name.toLowerCase())) return null;
        return channels.get(name.toLowerCase());
    }

    public Channel getChannel(String name, ChatPlayer player) {
        try {
            int no = Integer.parseInt(name);

            return player.getChannel(no);
        } catch (NumberFormatException ignored) {
        }

        if (channels.containsKey(name.toLowerCase())) return channels.get(name.toLowerCase());

        for (Channel channel : channels.values()) {
            if (channel.getTitle().equals(name)) return channel;
        }

        return null;
    }


    public Map<String, Channel> getChannels() {
        return channels;
    }

    public Channel getDefaultChannel() {
        if (!channels.containsKey(defaultChannel)) return null;
        return channels.get(defaultChannel.toLowerCase());
    }
}
