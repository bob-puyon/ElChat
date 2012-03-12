package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.JoinMessage;
import jp.commun.minecraft.elchat.message.Message;
import jp.commun.minecraft.elchat.message.QuitMessage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

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
    protected List<String> bans;
    protected List<String> mutes;

    public GameChannel(String name)
    {
        super(name);
        players = new HashMap<String, ChatPlayer>();
        bans = new ArrayList<String>();
        mutes = new ArrayList<String>();
    }

    @Override
    public void loadConfig(ConfigurationSection section) {
        super.loadConfig(section);

        area = section.getInt("area", 0);
        onlyWorld = section.getBoolean("only-world", false);
    }

    @Override
    public void saveConfig(ConfigurationSection section) {
        super.saveConfig(section);
        
        section.set("area", area);
        section.set("only-world", onlyWorld);
    }

    @Override
    public void processMessage(Message message)
    {
        if (message instanceof ChatMessage) {
            String formattedMessage = formatMessage((ChatMessage)message);
            
            Iterator<ChatPlayer> it = players.values().iterator();
            while (it.hasNext()) {
                ChatPlayer recipient = it.next();
                ChatPlayer sender = ((ChatMessage)message).getPlayer();
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
    }

    @Override
    public void join(ChatPlayer player)
    {
        if (players.containsKey(player.getName())) return;

        if (bans.contains(player.getName())) {
            player.sendMessage("Cannot join channel (You're banned)");
            return;
        }

        player.sendMessage("Joined Channel: [" + String.valueOf(player.getChannelNo(this)) + ". " + getTitle() + "]");

        Message message = new JoinMessage(player);
        sendMessage(message);

        if (announce) {
            String formattedMessage = formatMessage(message);

            for (ChatPlayer p: players.values()) {
                String channelNo = "";
                int no = p.getChannelNo(this);
                if (no != 0) {
                    channelNo = String.valueOf(no) + ". ";
                }

                p.sendMessage(formattedMessage.replace("%channelno", channelNo));
            }
        }

        players.put(player.getName(), player);
        player.addChannel(this);
    }

    @Override
    public void quit(ChatPlayer player)
    {
        if (!players.containsKey(player.getName())) return;
        player.sendMessage("Left Channel: [" + String.valueOf(player.getChannelNo(this)) + ". " + getTitle() + "]");

        Message message = new QuitMessage(player);
        sendMessage(message);

        if (announce) {
            String formattedMessage = formatMessage(message);

            for (ChatPlayer p: players.values()) {
                String channelNo = "";
                int no = p.getChannelNo(this);
                if (no != 0) {
                    channelNo = String.valueOf(no) + ". ";
                }

                p.sendMessage(formattedMessage.replace("%channelno", channelNo));
            }
        }

        players.remove(player.getName());
        player.removeChannel(this);
    }

    @Override
    public void chat(ChatPlayer player, String message)
    {
        if (!players.containsKey(player)) {
            player.sendMessage("You're not on that channel.");
            return;
        }

        if (mutes.contains(player.getName())) {
            player.sendMessage("");
            return;
        }

        Message m = new ChatMessage(player, message);
        sendMessage(m);
    }
    
    

    public Map<String, ChatPlayer> getPlayers()
    {
        return players;
    }
}
