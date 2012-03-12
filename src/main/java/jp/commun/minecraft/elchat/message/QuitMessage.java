package jp.commun.minecraft.elchat.message;

import jp.commun.minecraft.elchat.ChatPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/11
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public class QuitMessage extends PlayerMessage
{
    public QuitMessage(ChatPlayer player) {
        super(player);
    }

    public QuitMessage(String playerName) {
        super(playerName);
    }
}
