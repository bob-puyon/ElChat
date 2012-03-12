package jp.commun.minecraft.elchat.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/10
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class IRCCommandEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final String network;
    private final String channel;
    private final String nick;
    private final String commandName;
    private final String[] args;

    public IRCCommandEvent(String network, String channel, String nick, String commandName, String[] args) {
        this.network = network;
        this.channel = channel;
        this.nick = nick;
        this.commandName = commandName;
        this.args = args;
    }

    public String getNetwork() {
        return network;
    }

    public String getChannel() {
        return channel;
    }

    public String getNick() {
        return nick;
    }
    
    public String getCommandName()
    {
        return commandName;
    }

    public String[] getArgs()
    {
        return args;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
