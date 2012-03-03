package jp.commun.minecraft.elchat;

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

public class ElChatPlugin extends JavaPlugin
{
	private static ElChatPlugin plugin;
	private PermissionManager permissionsExManager;
	
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
		
		Log.info("ElChat enabled!");
		
	}

	@Override
	public void onDisable()
	{
		plugin.saveConfig();
		Log.info("ElChat disabled!");

	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
		if (sender instanceof ConsoleCommandSender) {
            if (!command.getName().equals("elchat")) return false;

            if (args.length == 0) {

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
}
