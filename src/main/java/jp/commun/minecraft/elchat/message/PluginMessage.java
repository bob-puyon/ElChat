package jp.commun.minecraft.elchat.message;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/11
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public class PluginMessage extends Message
{
    protected String message;

    public PluginMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
