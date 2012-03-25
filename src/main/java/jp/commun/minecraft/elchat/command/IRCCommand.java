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

package jp.commun.minecraft.elchat.command;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.irc.Bot;
import jp.commun.minecraft.util.StringUtils;
import jp.commun.minecraft.util.command.Command;
import jp.commun.minecraft.util.command.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class IRCCommand implements CommandHandler
{
    private final ElChatPlugin plugin;

    public IRCCommand(ElChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Command( names = { "elchat irc", "irc" }, permissions = { "elchat.irc" })
    public void irc(CommandSender sender, String commandName, String[] args)
    {
        Map<String, Bot> bots = plugin.getIRCManager().getBots();
        for (String name : bots.keySet()) {
            Bot bot = bots.get(name);
            sender.sendMessage(ChatColor.AQUA + "--- IRC Info: " + bot.getServer().getName() + " ---");
            sender.sendMessage(ChatColor.AQUA + "Host: " + ChatColor.WHITE + bot.getServer().getHost());
            if (bot.isConnected()) {
                sender.sendMessage(ChatColor.AQUA + "Status: " + ChatColor.WHITE + "connected.");
            } else {
                sender.sendMessage(ChatColor.AQUA + "Status: " + ChatColor.WHITE + "disconnected.");
            }

            Set<String> channels = bot.getServer().getChannels().keySet();
            sender.sendMessage(ChatColor.AQUA + "Channels: " + ChatColor.WHITE + StringUtils.join(new ArrayList<String>(channels), ", "));
        }
    }

    @Command( names = { "elchat irc connect", "irc connect"}, permissions = { "elchat.irc.connect" })
    public void connect(CommandSender sender, String commandName, String[] args)
    {
        plugin.getIRCManager().connect();
        sender.sendMessage("[ElChat] connecting...");
    }

    @Command( names = { "elchat irc disconnect", "irc disconnect"}, permissions = { "elchat.irc.disconnect" })
    public void disconnect(CommandSender sender, String commandName, String[] args)
    {
        plugin.getIRCManager().disconnect();
        sender.sendMessage("[ElChat] disconnecting...");
    }
}
