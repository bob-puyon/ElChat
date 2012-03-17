package jp.commun.minecraft.elchat.irc;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.IRCChannel;
import jp.commun.minecraft.elchat.message.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class IRCManager
{
    private ElChatPlugin plugin;
    private Map<String, Bot> bots;
    private volatile boolean isConnectScheduled = false;

    public IRCManager(ElChatPlugin plugin) {
        this.plugin = plugin;
        
        this.bots = new HashMap<String, Bot>();
        if (plugin.getConfig().contains("irc.networks")) {
            Set<String> keys = plugin.getConfig().getConfigurationSection("irc.networks").getKeys(false);
            if (keys != null && keys.size() > 0) {
                Iterator<String> it = keys.iterator();
                while(it.hasNext()) {
                    String name = it.next();
                    Server server = new Server(plugin.getConfig().getConfigurationSection("irc.networks." + name));
                    Bot bot = new Bot(server);
                    this.bots.put(name, bot);

                    for (IRCChannel channel: server.getChannels().values()) {
                        plugin.getChannelManager().addChannel(channel);
                    }
                }
            }
        }
    }
    

    public void reloadConfig()
    {
        for (Bot bot: bots.values()) {
            for (Channel channel: bot.getServer().getChannels().values()) {
                String sectionName = "irc.networks." + bot.getServer().getName() + ".channels." + channel.getName();
                if (!plugin.getConfig().contains(sectionName)) continue;
                channel.loadConfig(plugin.getConfig().getConfigurationSection(sectionName));
            }
        }
    }

    public void connect()
    {
        if (this.bots.size() > 0 && !isConnectScheduled) {
            for (Bot bot: bots.values()) {
                bot.setRetryEnabled(true);
            }
            isConnectScheduled = true;
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable(){
                @Override
                public void run() {
                    doConnect();
                }
            });
        }
    }

    public void disconnect()
    {
        Iterator<String> it = bots.keySet().iterator();
        boolean needReconnect = false;
        while (it.hasNext()) {
            String name = it.next();
            Bot bot = bots.get(name);
            bot.disconnect();
        }
    }
    
    public void sendMessage(Message message)
    {
        
    }
    
    public Map<String, String> getRoutes(String network, String channel)
    {
        if (this.bots.containsKey(network)) {
            Server server = this.bots.get(network).getServer();
            if (server.getChannels().containsKey(channel)) {
                server.getChannels().get(channel).getForwards();
            }
        }
        return null;
    }
    
    private void doConnect()
    {
        Iterator<String> it = bots.keySet().iterator();
        boolean needReconnect = false;
        while (it.hasNext()) {
            String name = it.next();
            Bot bot = bots.get(name);
            if (bot.isConnected() || !bot.isRetryEnabled()) continue;
            
            plugin.getLogger().info("connecting " + bot.getServer().getHost());

            try {
                bot.connect();
            } catch (IOException e) {
                plugin.getLogger().info("IRC: " + bot.getServer().getHost() + " connect failed.");
                
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

    public Bot getBot(String name)
    {
        if (bots.containsKey(name)) return bots.get(name);
        return null;
    }
    
    public Map<String, Bot> getBots() {
        return bots;
    }
}
