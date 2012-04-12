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

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.channel.GameChannel;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;
import jp.commun.minecraft.util.StringUtils;
import jp.commun.minecraft.util.command.Command;
import jp.commun.minecraft.util.command.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

public class ChannelCommand implements CommandHandler {
    private final ElChatPlugin plugin;

    public ChannelCommand(ElChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(names = {"1", "2", "3", "4", "5", "6", "7", "8", "9"}, allowConsole = false, desc = "switch channel.")
    public void message(CommandSender sender, String commandName, String[] args) {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        int channelNo = Integer.parseInt(commandName);
        Channel channel = player.getChannel(channelNo);
        if (channel == null) {
            sender.sendMessage("No such channel.");
            return;
        }

        if (args.length > 0) {
            String textMessage = StringUtils.join(args, " ");
            Message message = new ChatMessage(player, textMessage);
            channel.sendMessage(message);
        } else {
            player.setCurrentChannel(channel);
        }
    }

    @Command(names = {"channel list", "ch list"}, permissions = {"elchat.channel.list"}, allowConsole = false, desc = "active channel list")
    public void list(CommandSender sender, String commandName, String[] args) {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());

        if (args.length > 0) {
            Channel channel = plugin.getChannelManager().getChannel(args[1], player);
            if (channel != null && channel instanceof GameChannel) {
                sender.sendMessage("[" + player.getChannelNo(channel) + ". " + StringUtils.join((String[]) ((GameChannel) channel).getPlayers().keySet().toArray(), ", "));
            } else {
                sender.sendMessage("no such channel.");
            }
        } else {
            int i = 1;
            List<String> channels = new ArrayList<String>();
            for (Channel channel : player.getChannels().values()) {
                channels.add("[" + String.valueOf(i) + ". " + channel.getTitle() + "]");
                ++i;
            }
            sender.sendMessage(StringUtils.join(channels, " "));
        }
    }

    @Command(names = {"channel info", "ch info"}, permissions = {"elchat.channel.info"}, min = 1)
    public void info(CommandSender sender, String commandName, String[] args) {
        Channel channel;
        ChatPlayer player = null;
        if (sender instanceof ConsoleCommandSender) {
            channel = plugin.getChannelManager().getChannel(args[0]);
        } else {
            player = plugin.getPlayerManager().getPlayer(sender.getName());
            channel = plugin.getChannelManager().getChannel(args[0], player);
        }

        if (channel == null) {
            sender.sendMessage("no such channel.");
            return;
        }

        sender.sendMessage(ChatColor.AQUA + "--- Channel Info ---");
        sender.sendMessage(ChatColor.AQUA + "Name: " + ChatColor.WHITE + channel.getName());
        sender.sendMessage(ChatColor.AQUA + "Title: " + ChatColor.WHITE + channel.getTitle());

        sender.sendMessage(ChatColor.AQUA + "Announce: " + ChatColor.WHITE + String.valueOf(channel.isAnnounce()));
        sender.sendMessage(ChatColor.AQUA + "ForwardAnnounce: " + ChatColor.WHITE + String.valueOf(channel.isForwardAnnounce()));
        sender.sendMessage(ChatColor.AQUA + "Forwards: " + ChatColor.WHITE + StringUtils.join(channel.getForwards(), ", "));

        sender.sendMessage(ChatColor.AQUA + "RomaToHira: " + ChatColor.WHITE + String.valueOf(channel.isRomaToHira()));

        if (channel instanceof GameChannel) {
            GameChannel gameChannel = (GameChannel) channel;
            sender.sendMessage(ChatColor.AQUA + "AutoJoin: " + ChatColor.WHITE + String.valueOf(gameChannel.isAutoJoin()));
            sender.sendMessage(ChatColor.AQUA + "Moderation: " + ChatColor.WHITE + String.valueOf(gameChannel.isModeration()));
            if (gameChannel.getOwner() != null) {
                sender.sendMessage(ChatColor.AQUA + "Owner: " + ChatColor.WHITE + gameChannel.getOwner());
            } else {
                sender.sendMessage(ChatColor.AQUA + "Owner: " + ChatColor.WHITE + "none");
            }
            sender.sendMessage(ChatColor.AQUA + "Moderators: " + ChatColor.WHITE + StringUtils.join(gameChannel.getModerators(), ", "));
        }
    }

    @Command(names = {"channel who", "ch who"}, permissions = {"elchat.channel.who"}, min = 1)
    public void who(CommandSender sender, String commandName, String[] args) {
        Channel channel;
        ChatPlayer player = null;
        if (sender instanceof ConsoleCommandSender) {
            channel = plugin.getChannelManager().getChannel(args[0]);
        } else {
            player = plugin.getPlayerManager().getPlayer(sender.getName());
            channel = plugin.getChannelManager().getChannel(args[0], player);
        }

        if (channel == null || !(channel instanceof GameChannel)) {
            sender.sendMessage("no such channel.");
            return;
        }

        sender.sendMessage("[" + channel.getTitle() + "] " + StringUtils.join(new ArrayList<String>(((GameChannel) channel).getPlayers().keySet()), ", "));
    }

    @Command(names = {"channel join", "ch join", "join"}, permissions = {"elchat.channel.join"}, allowConsole = false, min = 1)
    public void join(CommandSender sender, String commandName, String[] args) {
        Channel channel = plugin.getChannelManager().getChannel(args[0]);
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        if (channel != null && channel instanceof GameChannel) {
            channel.join(player);
        } else if (channel == null && sender.hasPermission("elchat.channel.create")) {
            channel = new GameChannel(args[0]);
            ((GameChannel) channel).setOwner(player.getName());
            plugin.getChannelManager().addChannel(channel);
            channel.join(player);
        } else {
            sender.sendMessage("no such channel.");
        }
    }

    @Command(names = {"channel leave", "channel exit", "ch leave", "ch exit", "leave"}, permissions = {"elchat.channel.leave"}, allowConsole = false, min = 1)
    public void leave(CommandSender sender, String commandName, String[] args) {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        Channel channel = plugin.getChannelManager().getChannel(args[0], player);
        if (channel != null && channel instanceof GameChannel) {
            channel.quit(player);
            player.removeChannel(channel);
        } else {
            sender.sendMessage("no such channel.");
        }
    }

    @Command(names = {"channel ban", "ch ban"}, permissions = {"elchat.channel.ban"}, min = 2)
    public void ban(CommandSender sender, String commandName, String[] args) {
        Channel channel;
        ChatPlayer player = null;
        if (sender instanceof ConsoleCommandSender) {
            channel = plugin.getChannelManager().getChannel(args[0]);
        } else {
            player = plugin.getPlayerManager().getPlayer(sender.getName());
            channel = plugin.getChannelManager().getChannel(args[0], player);
        }

        if (channel == null || !(channel instanceof GameChannel)) {
            sender.sendMessage("no such channel.");
            return;
        }

        if (player != null && !((GameChannel) channel).isOwner(player) && !((GameChannel) channel).isModerator(player)) {
            sender.sendMessage("You are not the channel owner or moderator.");
            return;
        }

        ChatPlayer targetPlayer = plugin.getPlayerManager().getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage("no such player.");
            return;
        }
        ((GameChannel) channel).ban(targetPlayer);

        channel.announce(String.format("Player {0} banned by {1}.", targetPlayer.getName(), sender.getName()));
    }

    @Command(names = {"channel unban", "ch unban"}, permissions = {"elchat.channel.unban"}, min = 2)
    public void unban(CommandSender sender, String commandName, String[] args) {

    }

    public void mute(CommandSender sender, String commandName, String[] args) {

    }

    public void unmute(CommandSender sender, String commandName, String[] args) {

    }

    @Command(names = {"channel mod", "ch mod"}, permissions = {"elchat.channel.moderator"}, min = 2)
    public void moderator(CommandSender sender, String commandName, String[] args) {

    }

    @Command(names = {"channel unmod", "ch unmod"}, permissions = {"elchat.channel.unmoderator"}, min = 2)
    public void unmoderator(CommandSender sender, String commandName, String[] args) {

    }

    @Command(names = {"channel moderation", "ch moderation"}, permissions = {"elchat.channel.moderation"}, min = 2)
    public void moderation(CommandSender sender, String commandName, String[] args) {
        Channel channel;
        ChatPlayer player = null;
        if (sender instanceof ConsoleCommandSender) {
            channel = plugin.getChannelManager().getChannel(args[0]);
        } else {
            player = plugin.getPlayerManager().getPlayer(sender.getName());
            channel = plugin.getChannelManager().getChannel(args[0], player);
        }

        if (channel == null || !(channel instanceof GameChannel)) {
            sender.sendMessage("no such channel.");
            return;
        }

        if (player != null && !((GameChannel) channel).isOwner(player)) {
            sender.sendMessage("You are not the channel owner.");
            return;
        }

        ((GameChannel) channel).setModeration(!((GameChannel) channel).isModeration());
        if (((GameChannel) channel).isModeration()) {
            channel.announce(String.format("Channel moderation enabled by {0}.", sender.getName()));
        } else {
            channel.announce(String.format("Channel moderation disabled by {0}.", sender.getName()));
        }
    }

    @Command(names = {"channel owner", "ch owner"}, permissions = {"elchat.channel.owner"}, min = 1, max = 2)
    public void owner(CommandSender sender, String commandName, String[] args) {
        Channel channel;
        ChatPlayer player = null;
        if (sender instanceof ConsoleCommandSender) {
            channel = plugin.getChannelManager().getChannel(args[0]);
        } else {
            player = plugin.getPlayerManager().getPlayer(sender.getName());
            channel = plugin.getChannelManager().getChannel(args[0], player);
        }

        if (channel == null || !(channel instanceof GameChannel)) {
            sender.sendMessage("no such channel.");
            return;
        }

        if (args.length == 2) {
            if (player != null && !((GameChannel) channel).isOwner(player)) {
                sender.sendMessage("You are not the channel owner.");
                return;
            }

            ((GameChannel) channel).setOwner(args[1]);

            sender.sendMessage("Channel owner is " + args[1] + ".");
        } else {
            if (((GameChannel) channel).getOwner() != null) {
                sender.sendMessage("Channel owner is " + ((GameChannel) channel).getOwner() + ".");
            }
        }
    }

    @Command(names = {"channel set", "ch set"}, permissions = {"elchat.channel.set"}, min = 3)
    public void set(CommandSender sender, String commandName, String[] args) {
        Channel channel;
        ChatPlayer player = null;
        if (sender instanceof ConsoleCommandSender) {
            channel = plugin.getChannelManager().getChannel(args[0]);

            if (channel == null) {
                sender.sendMessage("no such channel.");
                return;
            }
        } else {
            player = plugin.getPlayerManager().getPlayer(sender.getName());
            channel = plugin.getChannelManager().getChannel(args[0], player);

            if (channel == null || !(channel instanceof GameChannel)) {
                sender.sendMessage("no such channel.");
                return;
            }

            if (!((GameChannel)channel).getOwner().equals(sender.getName())) {
                sender.sendMessage("You're not owner.");
                return;
            }
        }

        if (args[1].equals("title") && sender.hasPermission("elchat.channel.set.title")) {
            channel.setTitle(args[2]);
        } else if (args[1].equals("auto-join") && sender.hasPermission("elchat.channel.set.auto-join")) {
            channel.setAutoJoin(Boolean.parseBoolean(args[2]));
        } else if (args[1].equals("announce") && sender.hasPermission("elchat.channel.set.announce")) {
            channel.setAnnounce(Boolean.parseBoolean(args[2]));
        } else if (args[1].equals("forward-announce") && sender.hasPermission("elchat.channel.set.forward-announce")) {
            channel.setForwardAnnounce(Boolean.parseBoolean(args[2]));
        } else if (args[1].equals("roma-to-hira") && sender.hasPermission("elchat.channel.set.roma-to-hira")) {
            channel.setRomaToHira(Boolean.parseBoolean(args[2]));
        } else if (args[1].equals("password") && channel instanceof GameChannel && sender.hasPermission("elchat.channel.set.password")) {
            ((GameChannel)channel).setPassword(args[2]);
        } else {
            sender.sendMessage("Unknown property: " + args[1]);
            return;
        }

        sender.sendMessage("property updated.");
    }
}
