package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

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
        Log.info("onPlayerJoin");
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
	
	private String formatMessage(String format, String message, Player player)
	{
		PermissionManager permissionManager = plugin.getPermissionsExManager();
		if (permissionManager != null) {
			PermissionUser user = permissionManager.getUser(player);
			if (user != null) {
				if (user.getPrefix() != null) format = format.replace("%prefix", user.getPrefix());
				if (user.getSuffix() != null) format = format.replace("%suffix", user.getSuffix());
			}
		}
		format = format.replace("%world", player.getWorld().getName());
		format = format.replace("%message", message);
		format = format.replace("%player", player.getName());
		
		format = format.replaceAll("&([a-z0-9])", "\u00A7$1");
		
		return format;
	}
}
