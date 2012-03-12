package jp.commun.minecraft.elchat.message;

import jp.commun.minecraft.elchat.ChatPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/11
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlayerMessage extends Message
{
    protected ChatPlayer player;
    protected String playerName;

    public PlayerMessage(ChatPlayer player)
    {
        this.player = player;
    }

    public PlayerMessage(String playerName)
    {
        this.playerName = playerName;
    }

    public ChatPlayer getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }
}
