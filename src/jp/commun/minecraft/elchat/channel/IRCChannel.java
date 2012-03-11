package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.Message;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/05
 * Time: 20:11
 * To change this template use File | Settings | File Templates.
 */
public class IRCChannel extends Channel
{
    private boolean relay = false;
    private boolean sendGameJoinQuit = true;
    private boolean sendIRCJoinQuit = false;
    private boolean sendRelayJoinQuit = true;
    private com.sorcix.sirc.Channel serverChannel;

    public IRCChannel(String name)
    {
        super(name);
    }

    @Override
    public void loadConfig(ConfigurationSection section)
    {
        super.loadConfig(section);
        this.sendGameJoinQuit = section.getBoolean("sendGameJoinQuit", true);
        this.sendIRCJoinQuit = section.getBoolean("sendIRCJoinQuit", false);
        this.sendRelayJoinQuit = section.getBoolean("sendRelayJoinQuit", false);
    }

    @Override
    public void sendMessage(Message message) {
        super.sendMessage(message);

        if (!message.getChannel().equals(this) && serverChannel != null) {
            serverChannel.sendMessage(formatMessage(message));
        }
    }

    @Override
    public void join(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void quit(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRelay() {
        return relay;
    }

    public boolean isSendGameJoinQuit() {
        return sendGameJoinQuit;
    }

    public boolean isSendIRCJoinQuit() {
        return sendIRCJoinQuit;
    }

    public boolean isSendRelayJoinQuit() {
        return sendRelayJoinQuit;
    }

    public com.sorcix.sirc.Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(com.sorcix.sirc.Channel serverChannel) {
        this.serverChannel = serverChannel;
    }
}
