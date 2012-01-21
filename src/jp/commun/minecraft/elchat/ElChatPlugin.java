package jp.commun.minecraft.elchat;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ElChatPlugin extends JavaPlugin
{
	private static ElChatPlugin plugin;
	private ElChatPlayerListener playerListener;
	private ElChatServerListener serverListener;
	private PermissionManager permissionsExManager;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		
		this.loadConfiguration();
		
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
		config.addDefault("chat.message_format", "<%prefix%player%suffix&f> %message");
		config.addDefault("romatohira.enabled", true);
		ArrayList<String> ignoreWords = new ArrayList<String>(){{
			add("hi");
			add("tnt");
			add("wiki");
		}};
		
		config.addDefault("romatohira.ignore_words", ignoreWords);
		
		ArrayList<String> kanaWords = new ArrayList<String>(){{
			add("べっど");
			add("ぶろっく");
			add("ぷらぐいん");
			add("どあ");
			add("ちぇすと");
			add("はっち");
			add("すぽーん");
			add("さいと");
			add("まっぷ");
		}};
		config.addDefault("romatohira.kana_words", kanaWords);
		plugin.saveConfig();
	}

	public void disablePlugin()
	{
		this.getPluginLoader().disablePlugin(this);
	}
}
