package jp.commun.minecraft.elchat.message;

import jp.commun.minecraft.elchat.channel.Channel;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 6:36
 * To change this template use File | Settings | File Templates.
 */
public abstract class Message
{
    protected Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
