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

package jp.commun.minecraft.elchat.irc;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.channel.IRCChannel;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class IRCManager {
    private ElChatPlugin plugin;
    private Map<String, Bot> bots;
    private volatile boolean isConnectScheduled = false;

    public IRCManager(ElChatPlugin plugin) {
        this.plugin = plugin;

        this.bots = new HashMap<String, Bot>();
    }

    public void loadConfig() {
        if (plugin.getConfig().contains("irc.networks")) {
            ConfigurationSection networkSection = plugin.getConfig().getConfigurationSection("irc.networks");

            // 削除されたBOTを切断
            for (Bot bot: bots.values()) {
                if (!networkSection.contains(bot.getName())) {
                    bot.disconnect();
                    
                    for (IRCChannel channel : bot.getChannels().values()) {
                        plugin.getChannelManager().removeChannel(channel);
                    }
                    
                    bots.remove(bot.getName());
                }
            }

            for (String name : networkSection.getKeys(false)) {
                ConfigurationSection section = plugin.getConfig().getConfigurationSection("irc.networks." + name);
                Bot bot;
                if (bots.containsKey(name)) {
                    bot = bots.get(name);
                } else {
                    bot = new Bot();
                    bots.put(name, bot);
                }
                bot.loadConfig(section);

                // 無効になったので切断
                if (bot.isConnected() && !bot.isEnabled()) {
                    bot.disconnect();
                }
            }
        }
    }


    public void reloadConfig() {
        loadConfig();
    }

    public void connect() {
        if (this.bots.size() > 0 && !isConnectScheduled) {
            for (Bot bot : bots.values()) {
                bot.setRetryEnabled(true);
            }
            isConnectScheduled = true;
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable() {
                @Override
                public void run() {
                    doConnect();
                }
            });
        }
    }

    public void disconnect() {
        for (String name : bots.keySet()) {
            Bot bot = bots.get(name);
            bot.disconnect();
        }
    }

    private void doConnect() {
        Iterator<String> it = bots.keySet().iterator();
        boolean needReconnect = false;
        while (it.hasNext()) {
            String name = it.next();
            Bot bot = bots.get(name);
            if (bot.isConnected() || !bot.isRetryEnabled() || !bot.isEnabled()) continue;

            plugin.getLogger().info("connecting " + bot.getHost());

            try {
                bot.connect();
            } catch (IOException e) {
                plugin.getLogger().info("IRC: " + bot.getHost() + " connect failed.");

                e.printStackTrace();
                needReconnect = true;
            }
        }

        if (needReconnect) {
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable() {
                @Override
                public void run() {
                    doConnect();
                }
            }, 1200L); // 60 seconds
        } else {
            isConnectScheduled = false;
        }
    }

    public Bot getBot(String name) {
        if (bots.containsKey(name)) return bots.get(name);
        return null;
    }

    public Map<String, Bot> getBots() {
        return bots;
    }
}
