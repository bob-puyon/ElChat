package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.RomaToHira;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

public class PlayerListener implements Listener
{
    private final ElChatPlugin plugin;

    public PlayerListener(ElChatPlugin instance)
    {
        plugin = instance;
    }

    @EventHandler(priority= EventPriority.HIGH)
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		
		Player player = event.getPlayer();		
		String message = event.getMessage();
		
		if (plugin.getConfig().getBoolean("romatohira.enabled")) {
			String cleanMessage = message.replaceAll("&([a-z0-9])", "");
			if (!RomaToHira.hasHiragana(cleanMessage)) {
				String hiraMessage = RomaToHira.convert(cleanMessage);
				if (!hiraMessage.equals(cleanMessage)) {
					message += " &f(" + hiraMessage + ")";
				}
			}
		}
		
		message = this.formatMessage(plugin.getConfig().getString("chat.message_format"), message, player);
		
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
