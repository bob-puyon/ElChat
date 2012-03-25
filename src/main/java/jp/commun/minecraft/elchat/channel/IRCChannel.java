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

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.irc.IRCColor;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;
import org.bukkit.configuration.ConfigurationSection;

public class IRCChannel extends Channel
{
    private boolean relay = false;
    private boolean gameAnnounce = true;
    private boolean ircAnnounce = false;
    private boolean relayAnnounce = true;
    private com.sorcix.sirc.Channel serverChannel;

    public IRCChannel(String name)
    {
        super(name);
    }

    @Override
    public void loadConfig(ConfigurationSection section)
    {
        super.loadConfig(section);
        gameAnnounce = section.getBoolean("game-announce", true);
        ircAnnounce = section.getBoolean("irc-announce", false);
        relayAnnounce = section.getBoolean("relay-announce", false);
    }

    @Override
    public void saveConfig(ConfigurationSection section) {
        super.saveConfig(section);
        
        section.set("game-announce", gameAnnounce);
        section.set("irc-announce", ircAnnounce);
        section.set("relay-announce", relayAnnounce);
    }



    @Override
    public void processMessage(Message message)
    {
        if (serverChannel != null && !message.getChannel().equals(this)) {
            if (message instanceof ChatMessage) {
                serverChannel.sendMessage(IRCColor.toIRC(formatMessage(message)));
            } else {
                serverChannel.sendNotice(IRCColor.toIRC(formatMessage(message)));
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

    @Override
    public void chat(ChatPlayer player, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void broadcast(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRelay() {
        return relay;
    }

    public boolean isGameAnnounce() {
        return gameAnnounce;
    }

    public void setGameAnnounce(boolean gameAnnounce) {
        this.gameAnnounce = gameAnnounce;
    }

    public boolean isIrcAnnounce() {
        return ircAnnounce;
    }

    public void setIrcAnnounce(boolean ircAnnounce) {
        this.ircAnnounce = ircAnnounce;
    }

    public boolean isRelayAnnounce() {
        return relayAnnounce;
    }

    public void setRelayAnnounce(boolean relayAnnounce) {
        this.relayAnnounce = relayAnnounce;
    }

    public com.sorcix.sirc.Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(com.sorcix.sirc.Channel serverChannel) {
        this.serverChannel = serverChannel;
    }
}
