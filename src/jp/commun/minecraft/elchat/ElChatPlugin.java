package jp.commun.minecraft.elchat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ElChatPlugin extends JavaPlugin
{
	private static ElChatPlugin plugin;
	private Config config;
	private ElChatPlayerListener playerListener;
	private ElChatServerListener serverListener;
	private PermissionManager permissionsExManager;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		this.config = new Config(this);
		this.playerListener = new ElChatPlayerListener(this);
		this.serverListener = new ElChatServerListener(this);
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Monitor, this);
		
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.High, this);
		
		
		Plugin permissionsExPlugin = pm.getPlugin("PermissionsEx");
        if (permissionsExPlugin != null) {
            this.setPermissionsExPlugin(permissionsExPlugin);
        }
		
		Log.info("ElChat enabled!");
		
	}

	@Override
	public void onDisable()
	{
		this.config.save();
		Log.info("ElChat disabled!");

	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
		if (sender instanceof ConsoleCommandSender) {
            if (!command.getName().equals("elchat")) return false;

            if (args.length == 0) {

            }  else if (args[0].equals("reload")) {
            	config.load();
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
	
	public Config getConfig()
    {
        return config;
    }

	public void disablePlugin()
	{
		this.getPluginLoader().disablePlugin(this);
	}
}
