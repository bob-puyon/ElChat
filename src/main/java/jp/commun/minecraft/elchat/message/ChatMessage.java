package jp.commun.minecraft.elchat.message;

import jp.commun.minecraft.elchat.ChatPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/11
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public class ChatMessage extends PlayerMessage
{
    protected String message;

    public ChatMessage(ChatPlayer player, String message)
    {
        super(player);
        this.message = message;
    }

    public ChatMessage(String playerName, String message)
    {
        super(playerName);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
