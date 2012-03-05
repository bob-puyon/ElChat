package jp.commun.minecraft.elchat.irc;

import com.sorcix.sirc.IrcAdaptor;
import com.sorcix.sirc.IrcConnection;
import com.sorcix.sirc.NickNameException;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/05
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */
public class Bot extends IrcAdaptor
{
    public Server getServer() {
        return server;
    }

    private Server server;
    private IrcConnection connection;
    private int nickCount = 0;

    private boolean retryEnabled = true;
    
    public Bot(Server server) {
        this.server = server;
        this.connection = new IrcConnection(server.getHost(), server.getPort());
        this.connection.setNick(server.getNick());
        this.connection.addServerListener(this);
        this.connection.addMessageListener(this);
    }

    public void connect() throws IOException
    {
        if (isConnected()) return;
        Log.info("IRC:" + server.getName() + ": connecting " + server.getHost() + "...");
        this.retryEnabled = true;
        try {
            this.connection.connect();
        } catch (NickNameException e) {
            this.connection.setNick(server.getNick() + String.valueOf(nickCount));
            nickCount++;
            this.connect();
        }
    }

    public void disconnect()
    {
        this.retryEnabled = false;
        if (!isConnected()) return;
        Log.info("IRC:" + server.getName() + ": disconnect from " + server.getHost());
        this.connection.disconnect();
    }

    public boolean isConnected()
    {
        return this.connection.isConnected();
    }
    
    @Override
    public void onConnect(final IrcConnection connection)
    {
        Set<String> channels = this.server.getChannelNames();
        Iterator<String> it = channels.iterator();
        while (it.hasNext()) {
            String channelName = it.next();
            Log.info("IRC:" + server.getName() + ": joining " + channelName);
            connection.createChannel(channelName).join();
        }
    }

    @Override
    public void onDisconnect(final IrcConnection connection)
    {
        if (isRetryEnabled()) {
            ElChatPlugin.getPlugin().getIrcManager().connect();
        }
    }

    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    public void setRetryEnabled(boolean retryEnabled) {
        this.retryEnabled = retryEnabled;
    }
}
