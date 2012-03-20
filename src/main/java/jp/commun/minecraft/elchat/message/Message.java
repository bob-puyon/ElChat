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
    protected boolean forwardable = true;
    protected boolean forwardOnly = false;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isForwardable() {
        return forwardable;
    }

    public void setForwardable(boolean forwardable) {
        this.forwardable = forwardable;
    }

    public boolean isForwardOnly() {
        return forwardOnly;
    }

    public void setForwardOnly(boolean forwardOnly) {
        this.forwardOnly = forwardOnly;
    }
}
