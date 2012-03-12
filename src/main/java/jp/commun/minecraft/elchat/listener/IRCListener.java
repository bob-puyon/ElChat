package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Log;
import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.IRCChannel;
import jp.commun.minecraft.elchat.event.IRCCommandEvent;
import jp.commun.minecraft.elchat.event.IRCJoinEvent;
import jp.commun.minecraft.elchat.event.IRCMessageEvent;
import jp.commun.minecraft.elchat.event.IRCQuitEvent;
import jp.commun.minecraft.elchat.irc.Bot;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.JoinMessage;
import jp.commun.minecraft.elchat.message.Message;
import jp.commun.minecraft.elchat.message.QuitMessage;
import jp.commun.minecraft.util.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 6:28
 * To change this template use File | Settings | File Templates.
 */
public class IRCListener implements Listener
{
    private final ElChatPlugin plugin;

    public IRCListener(ElChatPlugin instance)
    {
        plugin = instance;
    }

    @EventHandler
    public void onIRCMessage(IRCMessageEvent event)
    {
        Log.info("onIRCMessage:" + event.getMessage());
        /*
        Message message = new Message(event.getNick(), event.getMessage());
        Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
        if (bot != null) {
            Channel channel = bot.getServer().getChannel(event.getChannel());
            if (channel != null) channel.sendMessage(message);
        }
        */
    }
    
    @EventHandler
    public void onIRCCommand(IRCCommandEvent event)
    {
        Log.info("onIRCCommand:" + event.getCommandName());
        if (event.getCommandName().equals("say")) {
            Message message = new ChatMessage(event.getNick(), StringUtils.join(event.getArgs(), " "));
            Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
            if (bot != null) {
                Channel channel = bot.getServer().getChannel(event.getChannel());
                if (channel != null) channel.sendMessage(message);
                if (channel == null) Log.info("channel is null!");
            }
        }
    }

    @EventHandler
    public void onIRCJoin(IRCJoinEvent event)
    {
        Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
        if (bot != null) {
            IRCChannel channel = bot.getServer().getChannel(event.getChannel());
            if (channel != null && channel.isIrcAnnounce()) {
                Message message = new JoinMessage(event.getNick());
                channel.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onIRCQuit(IRCQuitEvent event)
    {
        Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
        if (bot != null) {
            IRCChannel channel = bot.getServer().getChannel(event.getChannel());
            if (channel != null && channel.isIrcAnnounce()) {
                Message message = new QuitMessage(event.getNick());
                channel.sendMessage(message);
            }
        }
    }
}
