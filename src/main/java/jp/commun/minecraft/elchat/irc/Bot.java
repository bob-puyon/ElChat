/*
 * Copyright 2012 ayunyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.commun.minecraft.elchat.irc;

import com.sorcix.sirc.*;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.channel.IRCChannel;
import jp.commun.minecraft.elchat.event.IRCCommandEvent;
import jp.commun.minecraft.elchat.event.IRCJoinEvent;
import jp.commun.minecraft.elchat.event.IRCMessageEvent;
import jp.commun.minecraft.elchat.event.IRCQuitEvent;
import org.bukkit.event.Event;

import java.io.IOException;
import java.nio.charset.Charset;

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
        ElChatPlugin.getPlugin().getLogger().info("IRC:" + server.getName() + ": connecting " + server.getHost() + "...");
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
        ElChatPlugin.getPlugin().getLogger().info("IRC:" + server.getName() + ": disconnect from " + server.getHost());
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
            ElChatPlugin.getPlugin().getLogger().info("IRC:" + server.getName() + ": joining " + c.getName());
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
        ElChatPlugin.getPlugin().getLogger().info("Bot.onMessage:" + message);
        
        String coloredMessage = IRCColor.toGame(message);
        
        Event event;
        if (coloredMessage.substring(0, 1).equals(".")) {
            String[] commands = coloredMessage.substring(1).split(" ");
            String commandName = commands[0];
            String[] args = new String[commands.length - 1];
            System.arraycopy(commands, 1, args, 0, commands.length - 1);
            event = new IRCCommandEvent(server.getName(), channel.getName(), sender.getNick(), commandName, args);
        } else {
            event = new IRCMessageEvent(server.getName(), channel.getName(), sender.getNick(), coloredMessage);
        }
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onJoin(final IrcConnection irc, final com.sorcix.sirc.Channel channel, User user)
    {
        if (channel == null) return;
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
        for (String s : server.getChannelNames()) {
            IRCQuitEvent event = new IRCQuitEvent(server.getName(), s, user.getNick());
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
