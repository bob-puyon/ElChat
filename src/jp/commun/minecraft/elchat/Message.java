package jp.commun.minecraft.elchat;

import jp.commun.minecraft.elchat.channel.Channel;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 6:36
 * To change this template use File | Settings | File Templates.
 */
public class Message
{
    protected ChatPlayer player;
    protected String playerName;
    protected final String message;
    protected Channel channel;

    public Message(ChatPlayer player, String message) {
        this.player = player;
        this.message = message;
    }

    public Message(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    public ChatPlayer getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getMessage() {
        return message;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
