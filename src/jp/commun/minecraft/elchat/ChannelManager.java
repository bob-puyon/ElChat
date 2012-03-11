package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.DynmapChannel;
import jp.commun.minecraft.elchat.channel.GameChannel;
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
            if (channels.containsKey(name)) {
                channel = channels.get(name);
            } else if (name.equals("dynmap")) {
                channel = new DynmapChannel(name);
            } else {
                channel = new GameChannel(name);
            }
            channel.loadConfig(config.getConfigurationSection("channels." + name));
            if (!channels.containsKey(name)) channels.put(name, channel);
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
            if (!(channel instanceof GameChannel)) continue;
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
            channels.put(channel.getName(), channel);
        }
    }
    
    public Channel getChannel(String name)
    {
        if (!channels.containsKey(name)) return null;
        return channels.get(name);
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public Channel getDefaultChannel()
    {
        if (!channels.containsKey(defaultChannel)) return null;
        return channels.get(defaultChannel);
    }
}
