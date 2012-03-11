package jp.commun.minecraft.elchat.irc;

import com.sorcix.sirc.*;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Log;
import jp.commun.minecraft.elchat.channel.IRCChannel;
import jp.commun.minecraft.elchat.event.IRCCommandEvent;
import jp.commun.minecraft.elchat.event.IRCJoinEvent;
import jp.commun.minecraft.elchat.event.IRCMessageEvent;
import jp.commun.minecraft.elchat.event.IRCQuitEvent;
import org.bukkit.event.Event;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/05
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */
public class Bot extends IrcAdaptor
{
    public Server getServer() {
        return server;
    }

    private Server server;
    private IrcConnection connection;
    private int nickCount = 0;

    private boolean retryEnabled = true;
    
    public Bot(Server server) {
        this.server = server;
        this.connection = new IrcConnection(server.getHost(), server.getPort());
        this.connection.setNick(server.getNick());
        if (server.getCharset() != null && Charset.isSupported(server.getCharset())) {
            Charset charset = Charset.forName(server.getCharset());
            this.connection.setCharset(charset);
        }
        this.connection.addServerListener(this);
        this.connection.addMessageListener(this);
    }

    public void connect() throws IOException
    {
        if (isConnected()) return;
        Log.info("IRC:" + server.getName() + ": connecting " + server.getHost() + "...");
        this.retryEnabled = true;
        try {
            this.connection.connect();
        } catch (NickNameException e) {
            this.connection.setNick(server.getNick() + String.valueOf(nickCount));
            nickCount++;
            this.connect();
        }
    }

    public void disconnect()
    {
        this.retryEnabled = false;
        if (!isConnected()) return;
        Log.info("IRC:" + server.getName() + ": disconnect from " + server.getHost());
        this.connection.disconnect();
    }

    public boolean isConnected()
    {
        return this.connection.isConnected();
    }
    
    public void sendMessage()
    {
    }
    
    @Override
    public void onConnect(final IrcConnection connection)
    {

        for (IRCChannel c: server.getChannels().values()) {
            Log.info("IRC:" + server.getName() + ": joining " + c.getName());
            Channel channel = connection.createChannel(c.getName());
            channel.join();
            
            c.setServerChannel(channel);
        }
    }

    @Override
    public void onDisconnect(final IrcConnection connection)
    {
        if (isRetryEnabled()) {
            ElChatPlugin.getPlugin().getIRCManager().connect();
        }
    }

    @Override
    public void onMessage(final IrcConnection irc, final User sender, final com.sorcix.sirc.Channel channel, final String message)
    {
        Event event;
        if (message.substring(0, 1).equals(".")) {
            String[] commands = message.substring(1).split(" ");
            String commandName = commands[0];
            String[] args = new String[commands.length - 1];
            System.arraycopy(commands, 1, args, 0, commands.length - 1);
            event = new IRCCommandEvent(server.getName(), channel.getName(), sender.getNick(), commandName, args);
        } else {
            event = new IRCMessageEvent(server.getName(), channel.getName(), sender.getNick(), message);
        }
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onJoin(final IrcConnection irc, final com.sorcix.sirc.Channel channel, User user)
    {
        IRCJoinEvent event = new IRCJoinEvent(server.getName(), channel.getName(), user.getNick());
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onPart(final IrcConnection irc, final com.sorcix.sirc.Channel channel, final User user, String message)
    {
        IRCQuitEvent event = new IRCQuitEvent(server.getName(), channel.getName(), user.getNick());
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onKick(final IrcConnection irc, final com.sorcix.sirc.Channel channel, final User sender, final User user)
    {
        IRCQuitEvent event = new IRCQuitEvent(server.getName(), channel.getName(), user.getNick());
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onQuit(final IrcConnection irc, final User user, String message)
    {
        Iterator<String> it = server.getChannelNames().iterator();
        while (it.hasNext()) {
            IRCQuitEvent event = new IRCQuitEvent(server.getName(), it.next(), user.getNick());
            ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
        }
    }

    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    public void setRetryEnabled(boolean retryEnabled) {
        this.retryEnabled = retryEnabled;
    }
}
