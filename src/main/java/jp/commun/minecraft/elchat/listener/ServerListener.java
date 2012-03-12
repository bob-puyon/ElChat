package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class ServerListener implements Listener
{
	private final ElChatPlugin plugin;



    public ServerListener(ElChatPlugin instance)
    {
        plugin = instance;

    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPluginEnable(PluginEnableEvent event)
    {
        if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
            this.plugin.setPermissionsExPlugin(event.getPlugin());
        }
    }

    @EventHandler(priority= EventPriority.NORMAL)
    public void onPluginDisable(PluginDisableEvent event)
    {
        if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
            this.plugin.setPermissionsExPlugin(null);
        }
    }
}
