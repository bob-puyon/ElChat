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

package jp.commun.minecraft.elchat.channel;

import java.util.ArrayList;
import java.util.List;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.RomaToHira;
import jp.commun.minecraft.elchat.message.AnnounceMessage;
import jp.commun.minecraft.elchat.message.ChannelMessage;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;
import jp.commun.minecraft.elchat.message.PlayerMessage;
import jp.commun.minecraft.elchat.message.PluginMessage;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Channel {
    protected String name;
    protected String title;
    protected String type;
    protected boolean autoJoin;
    protected String messageFormat;
    protected String channelFormat;
    protected String romaToHiraFormat;
    protected boolean romaToHira;
    protected List<String> forwards;
    protected boolean announce = false;
    protected boolean forwardAnnounce = false;

    public Channel(String name) {
        this.name = name;
        this.forwards = new ArrayList<String>();
    }

    public void loadConfig(ConfigurationSection section) {
        this.name = section.getName();
        title = section.getString("title");
        type = section.getString("type");
        if (section.contains("auto-join")) this.autoJoin = section.getBoolean("auto-join", false);
        messageFormat = section.getString("message-format", null);
        channelFormat = section.getString("channel-format", null);
        if (section.contains("roma-to-hira")) romaToHira = section.getBoolean("roma-to-hira", false);
        if (section.contains("forwards")) {
            forwards = section.getStringList("forwards");
        }
        if (section.contains("announce")) announce = section.getBoolean("announce", false);
        if (section.contains("forward-announce")) forwardAnnounce = section.getBoolean("forward-announce", false);
    }

    public void saveConfig(ConfigurationSection section) {
        section.set("title", title);
        section.set("type", type);
        section.set("auto-join", autoJoin);
        section.set("message-format", messageFormat);
        section.set("channel-format", channelFormat);
        section.set("roma-to-hira", romaToHira);
        section.set("forwards", forwards);
        section.set("announce", announce);
        section.set("forward-announce", forwardAnnounce);
    }

    public void sendMessage(Message message) {
        if (message.getChannel() == null) {
            message.setChannel(this);

            if (romaToHira && message instanceof ChatMessage) {
                String textMessage = ((ChatMessage) message).getMessage();
                String cleanMessage = textMessage.replaceAll("&([a-z0-9])", "");
                if (!RomaToHira.hasHiragana(cleanMessage)) {
                    String hiraMessage = RomaToHira.convert(cleanMessage);
                    if (!hiraMessage.equals(cleanMessage)) {
                        String format = getRomaToHiraFormat();
                        format = format.replace("{message}", textMessage);
                        format = format.replace("{converted}", hiraMessage);

                        // Add Converted Field for Dynmap
                        ((ChatMessage) message).setConvertedMessage(hiraMessage);

                        ((ChatMessage) message).setMessage(format);
                    }
                }
            }

            if (!(message instanceof AnnounceMessage) || !message.isForwardOnly()) {
                processMessage(message);
            }

            forward(message);
        } else {
            if (!(message instanceof AnnounceMessage) || !message.isForwardOnly()) {
                processMessage(message);
            }
        }
    }

    protected void forward(Message message) {
        if (!message.isForwardable()) return;
        // routing another channel
        message.setForwardOnly(false);

        for (String channelName : forwards) {
            Channel channel = ElChatPlugin.getPlugin().getChannelManager().getChannel(channelName);
            if (channel == null || channel.equals(this)) continue;
            channel.sendMessage(message);
        }
    }

    public abstract void processMessage(Message message);

    @Override
    public boolean equals(Object o) {
        return (o instanceof Channel && ((Channel) o).getName().equals(getName()));
    }

    public String formatMessage(Message message) {
        if (message instanceof ChatMessage) {
            String format = getMessageFormat();
            format = formatPlayer(format, (PlayerMessage) message);
            format = formatChannel(format, message);


            String textMessage = ((ChatMessage) message).getMessage();
            // IDSP対策のため全角スペースを半角スペースに置換
            textMessage = textMessage.replace("　", "　");

            format = format.replace("{message}", textMessage);

            format = format.replaceAll("&([a-z0-9])", "\u00A7$1");

            return format;
        } else if (message instanceof ChannelMessage) {
            String format = getChannelFormat();
            format = formatChannel(format, message);
            format = format.replace("{message}", ((ChannelMessage) message).getMessage());
            format = format.replaceAll("&([a-z0-9])", "\u00A7$1");
            return format;
        } else if (message instanceof PluginMessage) {
            String format = ((PluginMessage) message).getMessage();
            return format.replaceAll("&([a-z0-9])", "\u00A7$1");
        }
        return "";
    }

    public String formatPlayer(String format, PlayerMessage message) {
        ChatPlayer sender = message.getPlayer();
        String playerGroup = "";
        String playerPrefix = "";
        String playerSuffix = "";
        if (sender != null) {
            format = format.replace("{world}", sender.getPlayer().getWorld().getName());
            format = format.replace("{player}", sender.getName());

            playerGroup = sender.getGroup();
            playerPrefix = sender.getPrefix();
            playerSuffix = sender.getSuffix();
        } else {
            format = format.replace("{world}", "");
            format = format.replace("{player}", ((ChatMessage) message).getPlayerName());
        }
        format = format.replace("{group}", playerGroup);
        format = format.replace("{prefix}", playerPrefix);
        format = format.replace("{suffix}", playerSuffix);
        return format;
    }

    public String formatChannel(String format, Message message) {
        format = format.replace("{channel}", message.getChannel().getTitle());

        if (!(this instanceof GameChannel)) {
            format = format.replace("{channelno}", "");
        }

        return format;
    }

    public String getMessageFormat() {
        if (messageFormat == null) return ElChatPlugin.getPlugin().getConfig().getString("global.message-format");
        return messageFormat;
    }

    public String getChannelFormat() {
        if (channelFormat == null) return ElChatPlugin.getPlugin().getConfig().getString("global.channel-format");
        return channelFormat;
    }

    public String getRomaToHiraFormat() {
        if (romaToHiraFormat == null) return ElChatPlugin.getPlugin().getConfig().getString("global.roma-to-hira-format");
        return romaToHiraFormat;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        if (title == null) return name;
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAutoJoin() {
        return autoJoin;
    }

    public void setAutoJoin(boolean autoJoin) {
        this.autoJoin = autoJoin;
    }

    public void addForward(Channel channel) {
        if (channel != null) {
            addForward(channel.getName());
        }
    }

    public void addForward(String channelName) {
        if (channelName != null && !forwards.contains(channelName)) {
            forwards.add(channelName);
        }
    }

    public List<String> getForwards() {
        return forwards;
    }

    public boolean isRomaToHira() {
        return romaToHira;
    }

    public void setRomaToHira(boolean romaToHira) {
        this.romaToHira = romaToHira;
    }


    public boolean isAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public boolean isForwardAnnounce() {
        return forwardAnnounce;
    }

    public void setForwardAnnounce(boolean forwardAnnounce) {
        this.forwardAnnounce = forwardAnnounce;
    }

    public abstract void join(ChatPlayer player);

    public abstract void quit(ChatPlayer player);

    public abstract void chat(ChatPlayer player, String message);

    public void broadcast(String message) {
        Message m = new ChannelMessage(message);
        sendMessage(m);
    }

    public void announce(String message) {
        Message m = new AnnounceMessage(message);
        m.setForwardable(forwardAnnounce);
        m.setForwardOnly(!announce);
        sendMessage(m);
    }
}
