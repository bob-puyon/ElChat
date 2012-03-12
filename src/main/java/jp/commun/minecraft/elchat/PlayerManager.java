package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.GameChannel;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 6:55
 * To change this template use File | Settings | File Templates.
 */
public class PlayerManager
{
    private ElChatPlugin plugin;
    private Map<String, ChatPlayer> players;
    private File playerDir;

    public PlayerManager(ElChatPlugin plugin)
    {
        this.plugin = plugin;
        this.players = new HashMap<String, ChatPlayer>();

        playerDir = new File(plugin.getDataFolder(), "players");
        if (!playerDir.exists()) playerDir.mkdirs();
    }

    public void loadConfig()
    {
    }

    public void reloadConfig()
    {
    }

    public void saveConfig()
    {
        Iterator<String> it = players.keySet().iterator();
        while (it.hasNext()) {
            ChatPlayer player = players.get(it.next());
            try {
                player.saveConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void loadPlayer(Player player)
    {
        Log.info("loadPlayer");
        ChatPlayer chatPlayer;
        if (!this.players.containsKey(player.getName())) {
            chatPlayer = new ChatPlayer(player);

            try {
                chatPlayer.loadConfig();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            players.put(chatPlayer.getName(), chatPlayer);
        } else {
            chatPlayer = players.get(player.getName());
        }
        
        Log.info("autojoining");
        for (Channel channel: plugin.getChannelManager().getChannels().values()) {
            Log.info("check channel:" + channel.getName());
            if (channel.isAutoJoin() && channel instanceof GameChannel) {
                if (!chatPlayer.hasChannel(channel)) chatPlayer.addChannel(channel);
                channel.join(chatPlayer);
            }
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event)
    {
        loadPlayer(event.getPlayer());    
    }

    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (players.containsKey(player.getName())) {
            ChatPlayer chatPlayer = players.get(player.getName());
            try {
                chatPlayer.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            players.remove(player.getName());

            for (Channel channel: chatPlayer.getChannels().values()) {
                channel.quit(chatPlayer);
            }
        }
    }
    
    public void onPlayerChat(PlayerChatEvent event)
    {
        ChatPlayer player = getPlayer(event.getPlayer().getName());
        Channel currentChannel = player.getCurrentChannel();
        if (currentChannel == null) currentChannel = plugin.getChannelManager().getDefaultChannel();
        currentChannel.chat(player, event.getMessage());
        event.setCancelled(true);
    }

    public ChatPlayer getPlayer(String name)
    {
        if (!players.containsKey(name)) {
            Player player = plugin.getServer().getPlayer(name);
            if (player == null) return null;
        }
        return players.get(name);
    }

    public Map<String, ChatPlayer> getPlayers() {
        return players;
    }

    public File getPlayerDir()
    {
        return playerDir;
    }
}
