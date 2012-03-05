package jp.commun.minecraft.elchat.irc;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class IrcManager
{
    private ElChatPlugin plugin;
    private Map<String, Bot> bots;
    private volatile boolean isConnectScheduled = false;

    public IrcManager(ElChatPlugin plugin) {
        this.plugin = plugin;
        
        this.bots = new HashMap<String, Bot>();
        Set<String> keys = plugin.getConfig().getConfigurationSection("irc.servers").getKeys(false);
        if (keys != null && keys.size() > 0) {
            Iterator<String> it = keys.iterator();
            while(it.hasNext()) {
                String name = it.next();
                Server server = new Server(plugin.getConfig().getConfigurationSection("irc.servers." + name));
                Bot bot = new Bot(server);
                this.bots.put(name, bot);
            }
        }
    }

    public void connect()
    {
        if (this.bots.size() > 0 && !isConnectScheduled) {
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
    
    private void doConnect()
    {
        Iterator<String> it = bots.keySet().iterator();
        boolean needReconnect = false;
        while (it.hasNext()) {
            String name = it.next();
            Bot bot = bots.get(name);
            if (bot.isConnected() || !bot.isRetryEnabled()) continue;

            try {
                bot.connect();
            } catch (IOException e) {
                Log.info("IRC: " + bot.getServer().getHost() + " connect failed.");
                
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

    public Map<String, Bot> getBots() {
        return bots;
    }
}
