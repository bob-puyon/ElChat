package jp.commun.minecraft.elchat.message;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/13
 * Time: 12:36
 * To change this template use File | Settings | File Templates.
 */
public class ChannelMessage extends Message
{
    protected String message;

    public ChannelMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
