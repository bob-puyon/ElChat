package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.Log;
import jp.commun.minecraft.elchat.RomaToHira;
import jp.commun.minecraft.elchat.message.*;
import org.bukkit.configuration.ConfigurationSection;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/06
 * Time: 7:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class Channel
{
    protected String name;
    protected String title;
    protected String type;
    protected boolean autoJoin;
    protected String messageFormat;
    protected String joinFormat;
    protected String quitFormat;
    protected boolean romaToHira;
    protected List<String> routes;
    protected boolean announce = false;
    protected boolean hopAnnounce = false;

    public Channel(String name)
    {
        this.name = name;
        this.routes = new ArrayList<String>();
    }
    
    public void loadConfig(ConfigurationSection section)
    {
        this.name = section.getName();
        title = section.getString("title");
        type = section.getString("type");
        if (section.contains("auto-join")) this.autoJoin = section.getBoolean("auto-join", false);
        messageFormat = section.getString("message-format", null);
        joinFormat = section.getString("join-format", null);
        quitFormat = section.getString("quit-format", null);
        if (section.contains("roma-to-hira")) romaToHira = section.getBoolean("roma-to-hira", false);
        if (section.contains("routes")) {
            routes = section.getStringList("routes");
        }
        if (section.contains("announce")) announce = section.getBoolean("announce", false);
        if (section.contains("hop-announce")) hopAnnounce = section.getBoolean("hop-announce", false);
    }
    
    public void saveConfig(ConfigurationSection section)
    {
        section.set("title", title);
        section.set("type", type);
        section.set("auto-join", autoJoin);
        section.set("message-format", messageFormat);
        section.set("join-format", joinFormat);
        section.set("quit-format", quitFormat);
        section.set("roma-to-hira", romaToHira);
        section.set("routes", routes);
        section.set("announce", announce);
        section.set("hop-announce", hopAnnounce);
    }
    
    public void sendMessage(Message message)
    {
        Log.info(this.getClass().getName() +  ".sendMessage[" + this.getName() + "]");
        if (message.getChannel() == null) {
            message.setChannel(this);

            if (romaToHira && message instanceof ChatMessage) {
                String textMessage = ((ChatMessage)message).getMessage();
                String cleanMessage = textMessage.replaceAll("&([a-z0-9])", "");
                if (!RomaToHira.hasHiragana(cleanMessage)) {
                    String hiraMessage = RomaToHira.convert(cleanMessage);
                    if (!hiraMessage.equals(cleanMessage)) {
                        ((ChatMessage)message).setMessage(textMessage + " &f(" + hiraMessage + ")");
                    }
                }
            }
            
            if ((!(message instanceof JoinMessage) && !(message instanceof QuitMessage)) || hopAnnounce) {
                route(message);
            }
            
        } else {
            Log.info("originalChannel: " + message.getChannel().getName());
        }
        processMessage(message);
    }
    
    protected void route(Message message)
    {
        // routing another channel
        Iterator<String> it = routes.iterator();
        while (it.hasNext()) {
            String channelName = it.next();
            Log.info("routing nexthop check:" + channelName);
            Channel channel = ElChatPlugin.getPlugin().getChannelManager().getChannel(channelName);
            if (channel == null) Log.info("getChannel returned null");
            if (channel == null || channel.equals(this)) continue;
            Log.info("nexthop:" + channel.getName());
            channel.sendMessage(message);
        }
    }
    
    public abstract void processMessage(Message message);
    
    @Override
    public boolean equals(Object o)
    {
        return (o instanceof Channel && ((Channel) o).getName().equals(getName()));
    }
    
    public String formatMessage(Message message)
    {
        if (message instanceof ChatMessage) {
            String format = getMessageFormat();
            format = getPlayerFormat(format, (PlayerMessage)message);
            

            String textMessage = ((ChatMessage)message).getMessage();
            // IDSP対策のため全角スペースを半角スペースに置換
            textMessage = textMessage.replace("　", "　");

            format = format.replace("{message}", textMessage);

            format = format.replaceAll("&([a-z0-9])", "\u00A7$1");

            return format;
        } else if (message instanceof JoinMessage) {
            String format = getJoinFormat();
            format = getPlayerFormat(format, (PlayerMessage)message);
            format = format.replaceAll("&([a-z0-9])", "\u00A7$1");
            return format;
        } else if (message instanceof QuitMessage) {
            String format = getQuitFormat();
            format = getPlayerFormat(format, (PlayerMessage)message);
            format = format.replaceAll("&([a-z0-9])", "\u00A7$1");
            return format;
        } else if (message instanceof PluginMessage) {
            String format = ((PluginMessage) message).getMessage();
            return format.replaceAll("&([a-z0-9])", "\u00A7$1");
        }
        return "";
    }
    
    public String getPlayerFormat(String format, PlayerMessage message)
    {
        ChatPlayer sender = message.getPlayer();
        String userPrefix = "";
        String userSuffix = "";
        if (sender != null) {
            format = format.replace("{world}", sender.getPlayer().getWorld().getName());
            format = format.replace("{player}", sender.getName());
            PermissionManager permissionManager = ElChatPlugin.getPlugin().getPermissionsExManager();
            if (permissionManager != null) {
                PermissionUser user = permissionManager.getUser(sender.getPlayer());
                if (user != null) {
                    if (user.getPrefix() != null) userPrefix = user.getPrefix();
                    if (user.getSuffix() != null) userSuffix = user.getSuffix();
                }
            }
        } else {
            format = format.replace("{world}", "");
            format = format.replace("{player}", ((ChatMessage)message).getPlayerName());
        }
        format = format.replace("{prefix}", userPrefix);
        format = format.replace("{suffix}", userSuffix);
        format = format.replace("{channel}", message.getChannel().getTitle());
        
        if (!(this instanceof GameChannel)) {
            format = format.replace("{channelno}", "");
        }
        
        return format;
    }
    
    public String getMessageFormat()
    {
        if (messageFormat == null) return ElChatPlugin.getPlugin().getConfig().getString("global.message-format");
        return messageFormat;
    }
    
    public String getJoinFormat()
    {
        if (joinFormat == null) return ElChatPlugin.getPlugin().getConfig().getString("global.join-format");
        return joinFormat;
    }
    
    public String getQuitFormat()
    {
        if (quitFormat == null) return ElChatPlugin.getPlugin().getConfig().getString("global.quit-format");
        return quitFormat;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        if (title == null) return name;
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAutoJoin() {
        return autoJoin;
    }

    public void setAutoJoin(boolean autoJoin) {
        this.autoJoin = autoJoin;
    }

    public List<String> getRoutes() {
        return routes;
    }

    public boolean isRomaToHira() {
        return romaToHira;
    }

    public void setRomaToHira(boolean romaToHira) {
        this.romaToHira = romaToHira;
    }

    public boolean isAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public boolean isHopAnnounce() {
        return hopAnnounce;
    }

    public void setHopAnnounce(boolean hopAnnounce) {
        this.hopAnnounce = hopAnnounce;
    }

    public abstract void join(ChatPlayer player);
    public abstract void quit(ChatPlayer player);
    public abstract void chat(ChatPlayer player, String message);
}
