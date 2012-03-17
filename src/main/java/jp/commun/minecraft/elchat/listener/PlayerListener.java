package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    private final ElChatPlugin plugin;

    public PlayerListener(ElChatPlugin instance)
    {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        plugin.getPlayerManager().onPlayerJoin(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        plugin.getPlayerManager().onPlayerQuit(event);
    }

    @EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		
        plugin.getPlayerManager().onPlayerChat(event);
	}
}
