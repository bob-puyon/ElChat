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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Bot extends IrcAdaptor {
    private boolean enabled;
    private String name;
    private String host;
    private int port;
    private String nick;
    private String charset;
    private Map<String, IRCChannel> channels;
    private IrcConnection connection;
    private int nickCount = 0;

    private boolean retryEnabled = true;

    public Bot() {
    }

    public void loadConfig(ConfigurationSection section) {
        this.enabled = section.getBoolean("enabled", true);
        this.name = section.getName();
        this.host = section.getString("host");
        this.port = section.getInt("port");
        this.nick = section.getString("nick");
        this.charset = section.getString("charset");

        this.channels = new HashMap<String, IRCChannel>();
        ConfigurationSection channelSection = section.getConfigurationSection("channels");
        if (channelSection != null) {
            // 設定から消えたチャンネルを削除
            for (IRCChannel channel : channels.values()) {
                if (!channelSection.contains(channel.getName())) {
                    channels.remove(channel.getName());
                    ElChatPlugin.getPlugin().getChannelManager().removeChannel(channel);
                    channel.getServerChannel().part();
                }
            }

            for (String channelName : channelSection.getKeys(false)) {
                IRCChannel channel;
                if (channels.containsKey(channelName)) {
                    channel = channels.get(channelName);
                } else {
                    channel = new IRCChannel(channelName);
                    this.channels.put(channelName, channel);
                    ElChatPlugin.getPlugin().getChannelManager().addChannel(channel);
                }
                channel.loadConfig(section.getConfigurationSection("channels." + channelName));
            }
        }
    }

    public void connect() throws IOException {
        if (connection == null) {
            this.connection = new IrcConnection(getHost(), getPort());
            this.connection.setNick(getNick());
            if (getCharset() != null && Charset.isSupported(getCharset())) {
                Charset charset = Charset.forName(getCharset());
                this.connection.setCharset(charset);
            }
            this.connection.addServerListener(this);
            this.connection.addMessageListener(this);
        }

        if (isConnected()) return;
        ElChatPlugin.getPlugin().getLogger().info("IRC:" + getName() + ": connecting " + getHost() + "...");
        this.retryEnabled = true;
        try {
            this.connection.connect();
        } catch (NickNameException e) {
            this.connection.setNick(getNick() + String.valueOf(nickCount));
            nickCount++;
            this.connect();
        }
    }

    public void disconnect() {
        this.retryEnabled = false;
        if (!isConnected()) return;
        ElChatPlugin.getPlugin().getLogger().info("IRC:" + getName() + ": disconnect from " + getHost());
        this.connection.disconnect();
    }

    public boolean isConnected() {
        return (this.connection != null && this.connection.isConnected());
    }

    public void sendMessage() {
    }

    public IRCChannel getChannel(String name) {
        if (channels.containsKey(name)) return channels.get(name);
        return null;
    }

    public Map<String, IRCChannel> getChannels() {
        return channels;
    }

    public Set<String> getChannelNames() {
        return channels.keySet();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getNick() {
        return nick;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public void onConnect(final IrcConnection connection) {

        for (IRCChannel c : getChannels().values()) {
            ElChatPlugin.getPlugin().getLogger().info("IRC:" + getName() + ": joining " + c.getName());
            Channel channel = connection.createChannel(c.getName());
            channel.join();

            c.setServerChannel(channel);
        }
    }

    @Override
    public void onDisconnect(final IrcConnection connection) {
        if (isRetryEnabled()) {
            ElChatPlugin.getPlugin().getIRCManager().connect();
        }
    }

    @Override
    public void onMessage(final IrcConnection irc, final User sender, final com.sorcix.sirc.Channel channel, final String message) {
        ElChatPlugin.getPlugin().getLogger().info("Bot.onMessage:" + message);

        String coloredMessage = IRCColor.toGame(message);

        Event event;
        if (coloredMessage.substring(0, 1).equals(".")) {
            String[] commands = coloredMessage.substring(1).split(" ");
            String commandName = commands[0];
            String[] args = new String[commands.length - 1];
            System.arraycopy(commands, 1, args, 0, commands.length - 1);
            event = new IRCCommandEvent(getName(), channel.getName(), sender.getNick(), commandName, args);
        } else {
            event = new IRCMessageEvent(getName(), channel.getName(), sender.getNick(), coloredMessage);
        }
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onJoin(final IrcConnection irc, final com.sorcix.sirc.Channel channel, User user) {
        if (channel == null) return;
        IRCJoinEvent event = new IRCJoinEvent(getName(), channel.getName(), user.getNick());
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onPart(final IrcConnection irc, final com.sorcix.sirc.Channel channel, final User user, String message) {
        IRCQuitEvent event = new IRCQuitEvent(getName(), channel.getName(), user.getNick());
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onKick(final IrcConnection irc, final com.sorcix.sirc.Channel channel, final User sender, final User user) {
        IRCQuitEvent event = new IRCQuitEvent(getName(), channel.getName(), user.getNick());
        ElChatPlugin.getPlugin().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void onQuit(final IrcConnection irc, final User user, String message) {
        for (String s : getChannelNames()) {
            IRCQuitEvent event = new IRCQuitEvent(getName(), s, user.getNick());
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
