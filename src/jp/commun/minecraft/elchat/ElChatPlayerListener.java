package jp.commun.minecraft.elchat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ElChatPlayerListener extends PlayerListener
{
    private final ElChatPlugin plugin;
    private Config config;


    public ElChatPlayerListener(ElChatPlugin instance)
    {
        plugin = instance;
        config = plugin.getConfig();
    }
    
	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		
		Player player = event.getPlayer();		
		String message = event.getMessage();
		
		if (this.config.romaToHiraEnabled) {
			String cleanMessage = message.replaceAll("&([a-z0-9])", "");
			if (!RomaToHira.hasHiragana(cleanMessage)) {
				String hiraMessage = RomaToHira.convert(cleanMessage);
				if (!hiraMessage.equals(cleanMessage)) {
					message += " &f(" + hiraMessage + ")";
				}
			}
		}
		
		message = this.formatMessage(this.config.messageFormat, message, player);
		
		// IDSP対策のため全角スペースを半角スペースに置換
		message = message.replace("　", "　");
		
		event.setFormat("%2$s");
		event.setMessage(message);
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
