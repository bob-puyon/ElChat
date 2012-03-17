package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.DynmapChannel;
import jp.commun.minecraft.elchat.channel.PluginChannel;
import jp.commun.minecraft.elchat.command.ChannelCommand;
import jp.commun.minecraft.elchat.command.ElChatCommand;
import jp.commun.minecraft.elchat.command.IRCCommand;
import jp.commun.minecraft.elchat.irc.IRCManager;
import jp.commun.minecraft.elchat.listener.DynmapListener;
import jp.commun.minecraft.elchat.listener.IRCListener;
import jp.commun.minecraft.elchat.listener.PlayerListener;
import jp.commun.minecraft.elchat.listener.ServerListener;
import jp.commun.minecraft.elchat.message.Message;
import jp.commun.minecraft.elchat.message.PluginMessage;
import jp.commun.minecraft.util.command.CommandManager;
import jp.commun.minecraft.util.command.CommandPermissionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ElChatPlugin extends JavaPlugin implements ElChatAPI
{
	private static ElChatPlugin plugin;
    private CommandManager commandManager;
    private IRCManager ircManager;
    private PlayerManager playerManager;
    private ChannelManager channelManager;
    private RomaToHiraData romaToHiraData;

    private PermissionManager permissionsExManager;
    private DynmapAPI dynmapAPI;
    
	@Override
	public void onEnable()
	{
		plugin = this;
		
		loadConfiguration();

        commandManager = new CommandManager();
        commandManager.register(new ElChatCommand(this));
        commandManager.register(new IRCCommand(this));
        commandManager.register(new ChannelCommand(this));

        playerManager = new PlayerManager(this);
        channelManager = new ChannelManager(this);
        channelManager.loadConfig();

        romaToHiraData = new RomaToHiraData(this);
        romaToHiraData.loadConfig();

        ircManager = new IRCManager(this);
        ircManager.connect();

		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new ServerListener(this), this);
        pm.registerEvents(new IRCListener(this), this);
		
		Plugin permissionsExPlugin = pm.getPlugin("PermissionsEx");
        if (permissionsExPlugin != null) {
            this.setPermissionsExPlugin(permissionsExPlugin);
        }
        
        Plugin dynmapPlugin = pm.getPlugin("dynmap");
        if (dynmapPlugin != null) {
            this.setDynmapPlugin(dynmapPlugin);
            if (dynmapAPI != null) {
                getLogger().info("found dynmap API!");
                pm.registerEvents(new DynmapListener(this), this);
            }
        }

        getLogger().info("ElChat enabled!");
	}

    @Override
	public void onDisable()
	{
		plugin.saveConfig();

        this.ircManager.disconnect();

        getLogger().info("ElChat disabled!");
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        try {
            commandManager.execute(sender, command, args);
        } catch (CommandPermissionException e) {
            sender.sendMessage("You don't have permission to do this.");
        } catch (CommandException e) {
            sender.sendMessage(e.getMessage());
        }

    	return false;
    }
	

	public static ElChatPlugin getPlugin()
	{
		return plugin;
	}
	
	public void setPermissionsExPlugin(Plugin permissionsExPlugin)
    {
        if (permissionsExPlugin != null) {
            getLogger().info("PermissionsEx detected! using: " + permissionsExPlugin.getDescription().getFullName());
            this.permissionsExManager = PermissionsEx.getPermissionManager();
        } else {
        	this.permissionsExManager = null;
        }
    }
	
	public PermissionManager getPermissionsExManager()
	{
		return this.permissionsExManager;
	}

    private void setDynmapPlugin(Plugin dynmapPlugin)
    {
        if (dynmapPlugin instanceof  DynmapAPI) {
            dynmapAPI = (DynmapAPI)dynmapPlugin;
            Channel channel = getChannelManager().getChannel("dynmap");
            if (channel == null) {
                plugin.getLogger().info("adding dynmap channel");
                channel = new DynmapChannel("dynmap");
                channel.addForward(getChannelManager().getDefaultChannel());
                getChannelManager().addChannel(channel);
            }
        } else {
            dynmapAPI = null;
        }
    }

    public DynmapAPI getDynmapAPI()
    {
        return dynmapAPI;
    }
	
	public void loadConfiguration()
	{
		FileConfiguration config = plugin.getConfig();
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}

	public void disablePlugin()
	{
		this.getPluginLoader().disablePlugin(this);
	}

    public IRCManager getIRCManager() {
        return ircManager;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public RomaToHiraData getRomaToHiraData() {
        return romaToHiraData;
    }

    @Override
    public void sendMessage(String channel, String message)
    {
        Channel c = channelManager.getChannel(channel);
        if (c != null) {
            c = new PluginChannel(channel);
            c.addForward(channelManager.getDefaultChannel());
            channelManager.addChannel(c);
        }
        Message m = new PluginMessage(message);
        c.sendMessage(m);
    }

    @Override
    public String getVersion()
    {
        return getDescription().getVersion();
    }
}
