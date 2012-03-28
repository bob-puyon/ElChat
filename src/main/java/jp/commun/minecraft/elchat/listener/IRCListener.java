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

package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.IRCChannel;
import jp.commun.minecraft.elchat.event.IRCCommandEvent;
import jp.commun.minecraft.elchat.event.IRCJoinEvent;
import jp.commun.minecraft.elchat.event.IRCMessageEvent;
import jp.commun.minecraft.elchat.event.IRCQuitEvent;
import jp.commun.minecraft.elchat.irc.Bot;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;
import jp.commun.minecraft.util.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IRCListener implements Listener {
    private final ElChatPlugin plugin;

    public IRCListener(ElChatPlugin instance) {
        plugin = instance;
    }

    @EventHandler
    public void onIRCMessage(IRCMessageEvent event) {
        if (plugin.getConfig().getBoolean("irc.default-say", false)) {
            Message message = new ChatMessage(event.getNick(), event.getMessage());
            Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
            if (bot != null) {
                Channel channel = bot.getChannel(event.getChannel());
                if (channel != null) channel.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onIRCCommand(IRCCommandEvent event) {
        if (event.getCommandName().equals("say")) {
            Message message = new ChatMessage(event.getNick(), StringUtils.join(event.getArgs(), " "));
            Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
            if (bot != null) {
                Channel channel = bot.getChannel(event.getChannel());
                if (channel != null) channel.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onIRCJoin(IRCJoinEvent event) {
        Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
        if (bot != null) {
            IRCChannel channel = bot.getChannel(event.getChannel());
            if (channel != null && channel.isIrcAnnounce()) {
                channel.announce(String.format("{0} joined channel.", event.getNick()));
            }
        }
    }

    @EventHandler
    public void onIRCQuit(IRCQuitEvent event) {
        Bot bot = plugin.getIRCManager().getBot(event.getNetwork());
        if (bot != null) {
            IRCChannel channel = bot.getChannel(event.getChannel());
            if (channel != null && channel.isIrcAnnounce()) {
                channel.announce(String.format("{0} left channel.", event.getNick()));
            }
        }
    }
}
