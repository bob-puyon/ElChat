package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.channel.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 6:55
 * To change this template use File | Settings | File Templates.
 */
public class ChannelManager
{
    private final ElChatPlugin plugin;
    private final File configFile;
    private final FileConfiguration config;
    private String defaultChannel;
    private Map<String, Channel> channels;

    public ChannelManager(ElChatPlugin plugin)
    {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "channels.yml");
        this.config = new YamlConfiguration();
        channels = new HashMap<String, Channel>();
    }

    public void loadConfig()
    {
        if (configFile.exists()) {
            try {
                this.config.load(configFile);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            config.setDefaults(YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/channels.yml")));
            config.options().copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if (config.contains("default-channel")) {
            defaultChannel = config.getString("default-channel");
        }
        
        Iterator<String> it = config.getConfigurationSection("channels").getKeys(false).iterator();
        while (it.hasNext()) {
            String name = it.next();
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

        if (defaultChannel == null || getDefaultChannel() == null) {
            defaultChannel = channels.keySet().iterator().next();
        }
    }

    public void reloadConfig()
    {
        loadConfig();
        saveConfig();
    }

    public void saveConfig()
    {
        config.set("channels", null);
        Iterator<String> it = channels.keySet().iterator();
        while (it.hasNext()) {
            Channel channel = channels.get(it.next());
            if (channel instanceof IRCChannel) continue;
            ConfigurationSection section = config.createSection("channels." + channel.getName());
            channel.saveConfig(section);
        }
        
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void createChannel()
    {

    }
    
    public void addChannel(Channel channel)
    {
        if (!channels.containsKey(channel.getName())) {
            plugin.getLogger().info("ChannelManager addChannel:" + channel.getName()) ;
            channels.put(channel.getName().toLowerCase(), channel);
        }
    }
    
    public Channel getChannel(String name)
    {
        if (!channels.containsKey(name.toLowerCase())) return null;
        return channels.get(name.toLowerCase());
    }
    
    public Channel getChannel(String name, ChatPlayer player)
    {
        try {
            int no = Integer.parseInt(name);
            
            return player.getChannel(no);
        } catch (NumberFormatException ignored) {
        }
        
        if (channels.containsKey(name.toLowerCase())) return channels.get(name.toLowerCase());
        
        for (Channel channel: channels.values()) {
            if (channel.getTitle().equals(name)) return channel;
        }
        
        return null;
    }


    public Map<String, Channel> getChannels() {
        return channels;
    }

    public Channel getDefaultChannel()
    {
        if (!channels.containsKey(defaultChannel)) return null;
        return channels.get(defaultChannel.toLowerCase());
    }
}
