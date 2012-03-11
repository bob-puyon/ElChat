package jp.commun.minecraft.elchat.irc;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.channel.IRCChannel;
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
    private String charset;
    private Map<String, IRCChannel> channels;

    public Server(ConfigurationSection section) {
        this.name = section.getName();
        this.host = section.getString("host");
        this.port = section.getInt("port");
        this.nick = section.getString("nick");
        this.charset = section.getString("charset");

        this.channels = new HashMap<String, IRCChannel>();
        Set<String> keys = section.getConfigurationSection("channels").getKeys(false);

        if (keys != null && keys.size() > 0) {
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String channelName = it.next();
                IRCChannel channel = new IRCChannel(channelName);
                channel.loadConfig(section.getConfigurationSection("channels." + channelName));
                this.channels.put(channelName, channel);
                ElChatPlugin.getPlugin().getChannelManager().addChannel(channel);
            }
        }
    }

    public IRCChannel getChannel(String name)
    {
        if (channels.containsKey(name)) return channels.get(name);
        return null;
    }
    
    public Map<String, IRCChannel> getChannels() {
        return channels;
    }

    public Set<String> getChannelNames()
    {
        return channels.keySet();
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

    public String getCharset() {
        return charset;
    }
}
