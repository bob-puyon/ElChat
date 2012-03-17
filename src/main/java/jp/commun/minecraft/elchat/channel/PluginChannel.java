package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.message.Message;

public class PluginChannel extends Channel
{
    public PluginChannel(String name) {
        super(name);
        this.type = "plugin";
    }

    @Override
    public void processMessage(Message message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void join(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void quit(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void chat(ChatPlayer player, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
