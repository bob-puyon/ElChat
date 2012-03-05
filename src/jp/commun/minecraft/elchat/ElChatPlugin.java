package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.irc.Bot;
import jp.commun.minecraft.elchat.irc.IrcManager;
import jp.commun.minecraft.elchat.listener.PlayerListener;
import jp.commun.minecraft.elchat.listener.ServerListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ElChatPlugin extends JavaPlugin
{
	private static ElChatPlugin plugin;
	private PermissionManager permissionsExManager;

    private IrcManager ircManager;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		
		this.loadConfiguration();

		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new ServerListener(this), this);
		
		Plugin permissionsExPlugin = pm.getPlugin("PermissionsEx");
        if (permissionsExPlugin != null) {
            this.setPermissionsExPlugin(permissionsExPlugin);
        }

        this.ircManager = new IrcManager(this);
        this.ircManager.connect();
		
		Log.info("ElChat enabled!");
	}

	@Override
	public void onDisable()
	{
		plugin.saveConfig();

        this.ircManager.disconnect();

		Log.info("ElChat disabled!");
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
		if (sender instanceof ConsoleCommandSender) {
            if (!command.getName().equals("elchat")) return false;

            if (args.length == 0) {
            } else if (args[0].equals("irc")) {
                if (args.length > 2) {
                    
                } else {
                    Map<String, Bot> bots = this.ircManager.getBots();
                    Iterator<String> it = bots.keySet().iterator();
                    while (it.hasNext()) {
                        String name = it.next();
                        Bot bot = bots.get(name);
                        sender.sendMessage(bot.getServer().getName() + ":" + bot.getServer().getHost());
                        if (bot.isConnected()) {
                            sender.sendMessage("Status: connected.");    
                        } else {
                            sender.sendMessage("Status: disconnected.");
                        }

                        Set<String> channels = bot.getServer().getChannels().keySet();
                        Iterator<String> channelIterator = channels.iterator();
                        sender.sendMessage("Channel Count: " + String.valueOf(channels.size()));
                        while (channelIterator.hasNext()) {
                            sender.sendMessage(channelIterator.next());
                        }
                    }
                }
            }  else if (args[0].equals("reload")) {
            	plugin.reloadConfig();
            }
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
            Log.info("PermissionsEx detected! using: " + permissionsExPlugin.getDescription().getFullName());
            this.permissionsExManager = PermissionsEx.getPermissionManager();
        } else {
        	this.permissionsExManager = null;
        }
    }
	
	public PermissionManager getPermissionsExManager()
	{
		return this.permissionsExManager;
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

    public IrcManager getIrcManager() {
        return ircManager;
    }
}
