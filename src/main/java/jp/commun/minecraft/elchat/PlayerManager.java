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
import jp.commun.minecraft.elchat.channel.GameChannel;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerManager {
    private ElChatPlugin plugin;
    private Map<String, ChatPlayer> players;
    private File playerDir;

    public PlayerManager(ElChatPlugin plugin) {
        this.plugin = plugin;
        this.players = new HashMap<String, ChatPlayer>();

        playerDir = new File(plugin.getDataFolder(), "players");
        if (!playerDir.exists()) {
            playerDir.mkdirs();
        }
    }

    public void loadConfig() {
    }

    public void reloadConfig() {
    }

    public void saveConfig() {
        for (String s : players.keySet()) {
            ChatPlayer player = players.get(s);
            try {
                player.saveConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void loadPlayer(Player player) {
        ChatPlayer chatPlayer;
        if (!this.players.containsKey(player.getName())) {
            chatPlayer = new ChatPlayer(player);

            try {
                chatPlayer.loadConfig();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            players.put(chatPlayer.getName(), chatPlayer);
        } else {
            chatPlayer = players.get(player.getName());
        }

        for (Channel channel : plugin.getChannelManager().getChannels().values()) {
            if (channel.isAutoJoin() && channel instanceof GameChannel) {
                if (!chatPlayer.hasChannel(channel)) chatPlayer.addChannel(channel);
                channel.join(chatPlayer);
            }
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayer(event.getPlayer());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (players.containsKey(player.getName())) {
            ChatPlayer chatPlayer = players.get(player.getName());
            try {
                chatPlayer.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
            players.remove(player.getName());

            Iterator<Channel> it = chatPlayer.getChannels().values().iterator();
            while (it.hasNext()) {
                Channel channel = it.next();
                channel.quit(chatPlayer);
                it.remove();
            }
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        ChatPlayer player = getPlayer(event.getPlayer().getName());
        Channel currentChannel = player.getCurrentChannel();
        if (currentChannel == null) currentChannel = plugin.getChannelManager().getDefaultChannel();
        currentChannel.chat(player, event.getMessage());
        event.setCancelled(true);
    }

    public ChatPlayer getPlayer(String name) {
        if (!players.containsKey(name)) {
            Player player = plugin.getServer().getPlayer(name);
            if (player == null) return null;
        }
        return players.get(name);
    }

    public Map<String, ChatPlayer> getPlayers() {
        return players;
    }

    public File getPlayerDir() {
        return playerDir;
    }
}
