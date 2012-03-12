package jp.commun.minecraft.elchat.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 6:17
 * To change this template use File | Settings | File Templates.
 */
public class IRCJoinEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final String network;
    private final String channel;
    private final String nick;

    public IRCJoinEvent(String network, String channel, String nick) {
        this.network = network;
        this.channel = channel;
        this.nick = nick;
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

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
