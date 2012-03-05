package jp.commun.minecraft.elchat.irc;

import jp.commun.minecraft.elchat.Log;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/05
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Server {
    private String name;
    private String host;
    private int port;
    private String nick;

    public Map<String, Channel> getChannels() {
        return channels;
    }
    
    public Set<String> getChannelNames()
    {
        return channels.keySet();
    }

    private Map<String, Channel> channels;

    public Server(ConfigurationSection section) {
        this.name = section.getName();
        this.host = section.getString("host");
        this.port = section.getInt("port");
        this.nick = section.getString("nick");

        this.channels = new HashMap<String, Channel>();
        Set<String> keys = section.getConfigurationSection("channels").getKeys(false);
        if (keys == null) {
            Log.info("channels keys null!");
        } else {
            Log.info("channels count:" + String.valueOf(keys.size()));
        }
        
        if (keys != null && keys.size() > 0) {
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String channelName = it.next();
                Channel channel = new Channel(section.getConfigurationSection("channels." + channelName));
                this.channels.put(channelName, channel);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getNick() {
        return nick;
    }
}
