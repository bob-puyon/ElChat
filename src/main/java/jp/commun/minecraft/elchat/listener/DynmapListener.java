package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Log;
import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.dynmap.DynmapWebChatEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/11
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class DynmapListener implements Listener
{
    private final ElChatPlugin plugin;

    public DynmapListener(ElChatPlugin instance)
    {
        plugin = instance;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDynmapWebChat(DynmapWebChatEvent event)
    {
        if (event.isCancelled()) return;

        Log.info("onDynmapWebChat");
        
        Message message = new ChatMessage(event.getName(), event.getMessage());
        Channel channel = plugin.getChannelManager().getChannel("dynmap");
        if (channel != null) {
            channel.sendMessage(message);

            event.setCancelled(true);
        }
    }
}
