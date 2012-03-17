package jp.commun.minecraft.elchat.message;

import jp.commun.minecraft.elchat.ChatPlayer;

public abstract class PlayerMessage extends Message
{
    protected ChatPlayer player;
    protected String playerName;

    protected PlayerMessage(ChatPlayer player) {
        this.player = player;
    }

    protected PlayerMessage(String playerName) {
        this.playerName = playerName;
    }

    public ChatPlayer getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }
}
