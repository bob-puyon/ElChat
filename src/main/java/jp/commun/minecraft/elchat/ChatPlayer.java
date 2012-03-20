package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.GameChannel;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 6:59
 * To change this template use File | Settings | File Templates.
 */
public class ChatPlayer
{
    private final Player player;
    private final String name;
    private final File configFile;
    private final FileConfiguration config;
    private Map<String, Channel> channels;
    private Channel currentChannel;

    public ChatPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.configFile = new File(ElChatPlugin.getPlugin().getPlayerManager().getPlayerDir(), name + ".yml");
        this.config = new YamlConfiguration();
        this.channels = new HashMap<String, Channel>();
    }

    public void loadConfig() throws IOException, InvalidConfigurationException
    {
        if (configFile.exists()) {
            config.load(configFile);
            Iterator<String> it = config.getStringList("channels").iterator();
            while (it.hasNext()) {
                Channel channel = ElChatPlugin.getPlugin().getChannelManager().getChannel(it.next());

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
        }
    }

    public void saveConfig() throws IOException
    {
        config.set("channels", new ArrayList<String>(channels.keySet()));
        config.save(configFile);
    }
    
    public void addChannel(Channel channel)
    {
        if (!channels.containsKey(channel)) {
            channels.put(channel.getName(), channel);
        }
    }

    public void removeChannel(Channel channel)
    {
        if (channels.containsKey(channel.getName())) {
            channels.remove(channel.getName());
        }
    }
    
    public void sendMessage(String message)
    {
        player.sendMessage(message);
    }

    public Player getPlayer()
    {
        return player;
    }
    
    public Location getLocation()
    {
        return  player.getLocation();
    }

    public String getName()
    {
        return name;
    }
    
    public Channel getChannel(String name)
    {
        if (channels.containsKey(name)) return channels.get(name);
        for (Channel channel: channels.values()) {
            if (channel.getTitle().equals(name)) return channel;
        }
        return null;
    }
    
    public Channel getChannel(int no)
    {
        int i = 1;
        for (Channel channel: channels.values()) {
            if (i == no) return channel;
            ++i;
        }
        return null;
    }

    public Map<String, Channel> getChannels()
    {
        return channels;
    }
    
    public int getChannelNo(Channel channel)
    {
        int i = 1;
        for (Channel c: channels.values()) {
            if (c.equals(channel)) return i;
            i++;
        }
        return 0;
    }

    public Channel getCurrentChannel()
    {
        if (currentChannel == null) {
            currentChannel = ElChatPlugin.getPlugin().getChannelManager().getDefaultChannel();
        }
        return currentChannel;
    }

    public void setCurrentChannel(Channel channel)
    {
        currentChannel = channel;

        getPlayer().sendMessage("Changed Channel: [" + String.valueOf(getChannelNo(channel)) + ". " + currentChannel.getTitle() + "]");
    }

    public boolean hasChannel(Channel channel)
    {
        return channels.containsValue(channel);
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
