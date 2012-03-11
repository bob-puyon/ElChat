package jp.commun.minecraft.elchat.command;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Message;
import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.util.StringUtils;
import jp.commun.minecraft.util.command.Command;
import jp.commun.minecraft.util.command.CommandHandler;
import org.bukkit.command.CommandSender;

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
            Message message = new Message(player, textMessage);
            channel.sendMessage(message);
        } else {
            player.setCurrentChannel(channel);
        }
    }

    @Command( names = { "channel list", "ch list" }, permissions = { "elchat.channel.list" }, allowConsole = false)
    public void list(CommandSender sender, String commandName, String[] args)
    {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        int i = 1;
        for (Channel channel: player.getChannels().values()) {
            sender.sendMessage(String.valueOf(i) + ": " + channel.getTitle());
        }
    }

    @Command( names = { "channel join", "ch join", "join" }, allowConsole = false)
    public void join(CommandSender sender, String commandName, String[] args)
    {

    }

    @Command( names = { "channel leave", "channel exit", "ch leave", "ch exit", "leave" }, allowConsole = false)
    public void leave(CommandSender sender, String commandName, String[] args)
    {

    }


}
