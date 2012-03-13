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
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/09
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class ChannelCommand implements CommandHandler
{
    private final ElChatPlugin plugin;

    public ChannelCommand(ElChatPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Command( names = { "1", "2", "3", "4", "5", "6", "7", "8", "9" }, allowConsole = false)
    public void message(CommandSender sender, String commandName, String[] args)
    {
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

    @Command( names = { "channel list", "ch list" }, permissions = { "elchat.channel.list" }, allowConsole = false)
    public void list(CommandSender sender, String commandName, String[] args)
    {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());

        if (args.length > 0) {
            Channel channel = plugin.getChannelManager().getChannel(args[1], player);
            if (channel != null && channel instanceof GameChannel) {
                sender.sendMessage("[" + player.getChannelNo(channel) + ". " + StringUtils.join((String[])((GameChannel) channel).getPlayers().keySet().toArray(), ", "));
            } else {
                sender.sendMessage("no such channel.");
            }
        } else {
            int i = 1;
            List<String> channels = new ArrayList<String>();
            for (Channel channel: player.getChannels().values()) {
                channels.add("[" + String.valueOf(i) + ". " + channel.getTitle() + "]");
                ++i;
            }
            sender.sendMessage(StringUtils.join(channels, " "));
        }
    }

    @Command( names = { "channel join", "ch join", "join" }, permissions = { "elchat.channel.join" }, allowConsole = false, min = 1)
    public void join(CommandSender sender, String commandName, String[] args)
    {
        Channel channel = plugin.getChannelManager().getChannel(args[0]);
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        if (channel != null && channel instanceof GameChannel) {
            channel.join(player);
        } else if (channel == null && sender.hasPermission("elchat.channel.create")) {
            channel = new GameChannel(args[1]);
            ((GameChannel)channel).setOwner(player.getName());
            plugin.getChannelManager().addChannel(channel);
            channel.join(player);
        } else {
            sender.sendMessage("no such channel.");
        }
    }

    @Command( names = { "channel leave", "channel exit", "ch leave", "ch exit", "leave" }, permissions = { "elchat.channel.leave" }, allowConsole = false, min = 1)
    public void leave(CommandSender sender, String commandName, String[] args)
    {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        Channel channel = plugin.getChannelManager().getChannel(args[0]);
        if (channel != null && channel instanceof GameChannel) {
            channel.quit(player);
            player.removeChannel(channel);
        } else {
            sender.sendMessage("no such channel.");
        }
    }

    @Command( names = { "channel ban", "ch ban" }, permissions = { "elchat.channel.ban" }, min = 2)
    public void ban(CommandSender sender, String commandName, String[] args)
    {
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

        if (player != null && !((GameChannel)channel).isOwner(player) && !((GameChannel) channel).isModerator(player)) {
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

    @Command( names = { "channel unban", "ch unban" }, permissions = { "elchat.channel.unban" }, min = 2)
    public void unban(CommandSender sender, String commandName, String[] args)
    {

    }

    public void mute(CommandSender sender, String commandName, String[] args)
    {

    }

    public void unmute(CommandSender sender, String commandName, String[] args)
    {

    }

    @Command( names = { "channel mod", "ch mod" }, permissions = { "elchat.channel.moderator" }, min = 2)
    public void moderator(CommandSender sender, String commandName, String[] args)
    {

    }

    @Command( names = { "channel unmod", "ch unmod" }, permissions = { "elchat.channel.unmoderator" }, min = 2)
    public void unmoderator(CommandSender sender, String commandName, String[] args)
    {

    }

    @Command( names = { "channel moderation", "ch moderation" }, permissions = { "elchat.channel.moderation" }, min = 2)
    public void moderation(CommandSender sender, String commandName, String[] args)
    {
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

        if (player != null && !((GameChannel)channel).isOwner(player)) {
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

    @Command( names = { "channel owner", "ch owner" }, permissions = { "elchat.channel.owner" }, min = 1, max = 2)
    public void owner(CommandSender sender, String commandName, String[] args)
    {
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
            if (player != null && !((GameChannel)channel).isOwner(player)) {
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

    @Command( names = { "channel set", "ch set" }, permissions = { "elchat.channel.set" }, min = 3)
    public void set(CommandSender sender, String commandName, String[] args)
    {
        if (sender instanceof ConsoleCommandSender) {
            Channel channel = plugin.getChannelManager().getChannel(args[0]);
            if (channel != null) {
                sender.sendMessage("no such channel.");
                return;
            }

            if (args[1].equals("title")) {
                channel.setTitle(args[2]);
            } else if (args[1].equals("auto-join")) {
                channel.setAutoJoin(Boolean.parseBoolean(args[3]));
            } else if (args[1].equals("announce")) {
                channel.setAnnounce(Boolean.parseBoolean(args[3]));
            } else if (args[1].equals("hop-announce")) {
                channel.setForwardAnnounce(Boolean.parseBoolean(args[3]));
            } else if (args[1].equals("roma-to-hira")) {
                channel.setRomaToHira(Boolean.parseBoolean(args[3]));

            } else {
                sender.sendMessage("Unknown property: " + args[1]);
                return;
            }

            sender.sendMessage("property updated.");
        }
    }
}
