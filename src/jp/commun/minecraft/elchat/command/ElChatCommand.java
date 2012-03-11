package jp.commun.minecraft.elchat.command;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.util.command.Command;
import jp.commun.minecraft.util.command.CommandHandler;
import org.bukkit.command.CommandSender;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/07
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
public class ElChatCommand implements CommandHandler
{
    private final ElChatPlugin plugin;

    public ElChatCommand(ElChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(names = { "elchat reload" }, permissions = { "elchat.reload"})
    public void reload(CommandSender sender, String commandName, String[] args)
    {
        plugin.reloadConfig();
        plugin.getChannelManager().reloadConfig();
        plugin.getRomaToHiraData().reloadConfig();
        sender.sendMessage("reloaded.");
    }
}
