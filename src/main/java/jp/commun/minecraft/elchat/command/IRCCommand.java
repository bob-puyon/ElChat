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
