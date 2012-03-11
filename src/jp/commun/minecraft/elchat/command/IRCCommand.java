package jp.commun.minecraft.elchat.command;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.irc.Bot;
import jp.commun.minecraft.util.command.Command;
import jp.commun.minecraft.util.command.CommandHandler;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/07
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
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
        Iterator<String> it = bots.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            Bot bot = bots.get(name);
            sender.sendMessage(bot.getServer().getName() + ":" + bot.getServer().getHost());
            if (bot.isConnected()) {
                sender.sendMessage("Status: connected.");
            } else {
                sender.sendMessage("Status: disconnected.");
            }

            Set<String> channels = bot.getServer().getChannels().keySet();
            Iterator<String> channelIterator = channels.iterator();
            sender.sendMessage("IRCChannel Count: " + String.valueOf(channels.size()));
            while (channelIterator.hasNext()) {
                sender.sendMessage(channelIterator.next());
            }
        }
    }
}
