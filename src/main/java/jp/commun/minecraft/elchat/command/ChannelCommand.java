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

    @Command( names = { "channel join", "ch join", "join" }, permissions = { "elchat.channel.join" }, allowConsole = false)
    public void join(CommandSender sender, String commandName, String[] args)
    {

    }

    @Command( names = { "channel leave", "channel exit", "ch leave", "ch exit", "leave" }, permissions = { "elchat.channel.leave" }, allowConsole = false)
    public void leave(CommandSender sender, String commandName, String[] args)
    {   
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
                channel.setHopAnnounce(Boolean.parseBoolean(args[3]));
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
