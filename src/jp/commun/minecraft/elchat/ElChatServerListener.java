package jp.commun.minecraft.elchat;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class ElChatServerListener extends ServerListener
{
	private final ElChatPlugin plugin;



    public ElChatServerListener(ElChatPlugin instance)
    {
        plugin = instance;

    }
    
    public void onPluginEnable(PluginEnableEvent event)
    {
        if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
            this.plugin.setPermissionsExPlugin(event.getPlugin());
        }
    }

    public void onPluginDisable(PluginDisableEvent event)
    {
        if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
            this.plugin.setPermissionsExPlugin(null);
        }
    }
}
