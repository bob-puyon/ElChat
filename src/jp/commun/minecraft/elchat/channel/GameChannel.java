package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.Message;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/08
 * Time: 5:29
 * To change this template use File | Settings | File Templates.
 */
public class GameChannel extends Channel
{
    protected Map<String, ChatPlayer> players;
    protected int area = 0;
    protected boolean onlyWorld = false;

    public GameChannel(String name)
    {
        super(name);
        players = new HashMap<String, ChatPlayer>();
    }

    @Override
    public void loadConfig(ConfigurationSection section) {
        super.loadConfig(section);

        area = section.getInt("area", 0);
        onlyWorld = section.getBoolean("only-world", false);
    }

    @Override
    public void saveConfig(ConfigurationSection section) {
        super.saveConfig(section);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(Message message) {
        super.sendMessage(message);

        String formattedMessage = formatMessage(message);
        
        Iterator<ChatPlayer> it = players.values().iterator();
        while (it.hasNext()) {
            ChatPlayer recipient = it.next();
            ChatPlayer sender = message.getPlayer();
            if (onlyWorld && sender != null && !recipient.getPlayer().getWorld().equals(sender.getPlayer().getWorld())) continue;
            if (area != 0 && sender != null && recipient.getLocation().distance(sender.getLocation()) > area) continue;

            String channelNo = "";
            int no = recipient.getChannelNo(this);
            if (no != 0) {
                channelNo = String.valueOf(no) + ". ";
            }
            recipient.sendMessage(formattedMessage.replace("%channelno", channelNo));
        }
    }

    @Override
    public void join(ChatPlayer player)
    {
        if (!players.containsKey(player.getName())) {
            player.sendMessage("Joined Channel: [" + String.valueOf(player.getChannelNo(this)) + ". " + getTitle() + "]");
            players.put(player.getName(), player);
        }
    }

    @Override
    public void quit(ChatPlayer player)
    {
        if (players.containsKey(player.getName())) {
            player.sendMessage("Left Channel: [" + String.valueOf(player.getChannelNo(this)) + ". " + getTitle() + "]");
            players.remove(player.getName());
        }
    }

    public Map<String, ChatPlayer> getPlayers()
    {
        return players;
    }
}
