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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.GameChannel;
import jp.commun.minecraft.util.permission.Permission;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ChatPlayer {
    private final Player player;
    private final String name;
    private final File configFile;
    private final FileConfiguration config;
    private Map<String, Channel> channels;
    private Channel currentChannel;
    private String prefix;
    private String suffix;

    public ChatPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.configFile = new File(ElChatPlugin.getPlugin().getPlayerManager().getPlayerDir(), name + ".yml");
        this.config = new YamlConfiguration();
        this.channels = new HashMap<String, Channel>();
    }

    public void loadConfig() throws IOException, InvalidConfigurationException {
        if (configFile.exists()) {
            config.load(configFile);
            for (String channelName : config.getStringList("channels")) {
                Channel channel = ElChatPlugin.getPlugin().getChannelManager().getChannel(channelName);
                if (channel == null) {
                    if (player.hasPermission("elchat.channel.create")) {
                        channel = new GameChannel(channelName);
                        ((GameChannel) channel).setOwner(player.getName());
                        ElChatPlugin.getPlugin().getChannelManager().addChannel(channel);
                    } else {
                        continue;
                    }
                }

                channel.join(this);
                if (currentChannel == null && channel instanceof GameChannel) {
                    currentChannel = channel;
                }
            }

            if (config.contains("default-channel")) {
                String defaultChannel = config.getString("default-string");
                if (channels.containsKey(defaultChannel)) {
                    currentChannel = channels.get(defaultChannel);
                }
            }

            suffix = config.getString("suffix");
            prefix = config.getString("prefix");
        }
    }

    public void saveConfig() throws IOException {
        config.set("channels", new ArrayList<String>(channels.keySet()));
        config.set("prefix", prefix);
        config.set("suffix", suffix);
        config.save(configFile);
    }

    public void addChannel(Channel channel) {
        if (!channels.containsKey(channel)) {
            channels.put(channel.getName(), channel);
        }
    }

    public void removeChannel(Channel channel) {
        if (channels.containsKey(channel.getName())) {
            channels.remove(channel.getName());
        }
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return player.getLocation();
    }

    public String getName() {
        return name;
    }

    public Channel getChannel(String name) {
        if (channels.containsKey(name)) return channels.get(name);
        for (Channel channel : channels.values()) {
            if (channel.getTitle().equals(name)) return channel;
        }
        return null;
    }

    public Channel getChannel(int no) {
        int i = 1;
        for (Channel channel : channels.values()) {
            if (i == no) return channel;
            ++i;
        }
        return null;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public int getChannelNo(Channel channel) {
        int i = 1;
        for (Channel c : channels.values()) {
            if (c.equals(channel)) return i;
            i++;
        }
        return 0;
    }

    public Channel getCurrentChannel() {
        if (currentChannel == null) {
            currentChannel = ElChatPlugin.getPlugin().getChannelManager().getDefaultChannel();
        }
        return currentChannel;
    }

    public void setCurrentChannel(Channel channel) {
        currentChannel = channel;

        String change_confirm = "発言チャンネルを次のチャネルに変更しました: [" + String.valueOf(getChannelNo(channel)) + ". " + currentChannel.getTitle() + "] - Changed Channel";
        getPlayer().sendMessage( change_confirm.replaceAll("&([a-z0-9])", "\u00A7$1") );
    }

    public boolean hasChannel(Channel channel) {
        return channels.containsValue(channel);
    }

    public String getPrefix() {
        if (prefix != null && prefix.length() > 0) return prefix;
        Permission permission = ElChatPlugin.getPlugin().getPermission();
        if (permission != null) {
            String result = permission.getPlayerPrefix(player);
            if (result != null) return result;
        }
        return "";
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        if (suffix != null && suffix.length() > 0) return suffix;
        Permission permission = ElChatPlugin.getPlugin().getPermission();
        if (permission != null) {
            String result = permission.getPlayerSuffix(player);
            if (result != null) return result;
        }
        return "";
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getGroup() {
        Permission permission = ElChatPlugin.getPlugin().getPermission();
        if (permission != null) {
            String result = permission.getGroup(player);
            if (result != null) return result;
        }
        return "";
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return player.equals(obj);
    }
}
