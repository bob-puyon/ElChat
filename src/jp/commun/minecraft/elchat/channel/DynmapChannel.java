package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Message;
import org.dynmap.DynmapAPI;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/08
 * Time: 5:30
 * To change this template use File | Settings | File Templates.
 */
public class DynmapChannel extends Channel
{
    public DynmapChannel(String name)
    {
        super(name);
    }

    @Override
    public void sendMessage(Message message) {
        super.sendMessage(message);

        if (!message.getChannel().equals(this)) {
            DynmapAPI dynmapAPI = ElChatPlugin.getPlugin().getDynmapAPI();
            if (dynmapAPI != null) {
                dynmapAPI.sendBroadcastToWeb(null, formatMessage(message));
            }
        }
    }

    @Override
    public void join(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void quit(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
