package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.ElChatPlugin;
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
    protected String channelFormat;
    protected boolean romaToHira;
    protected List<String> forwards;
    protected boolean announce = false;
    protected boolean forwardAnnounce = false;

    public Channel(String name)
    {
        this.name = name;
        this.forwards = new ArrayList<String>();
    }
    
    public void loadConfig(ConfigurationSection section)
    {
        this.name = section.getName();
        title = section.getString("title");
        type = section.getString("type");
        if (section.contains("auto-join")) this.autoJoin = section.getBoolean("auto-join", false);
        messageFormat = section.getString("message-format", null);
        channelFormat = section.getString("chanel-format", null);
        if (section.contains("roma-to-hira")) romaToHira = section.getBoolean("roma-to-hira", false);
        if (section.contains("forwards")) {
            forwards = section.getStringList("forwards");
        }
        if (section.contains("announce")) announce = section.getBoolean("announce", false);
        if (section.contains("forward-announce")) forwardAnnounce = section.getBoolean("forward-announce", false);
    }
    
    public void saveConfig(ConfigurationSection section)
    {
        section.set("title", title);
        section.set("type", type);
        section.set("auto-join", autoJoin);
        section.set("message-format", messageFormat);
        section.set("join-format", channelFormat);
        section.set("roma-to-hira", romaToHira);
        section.set("forwards", forwards);
        section.set("announce", announce);
        section.set("forward-announce", forwardAnnounce);
    }
    
    public void sendMessage(Message message)
    {
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
            
            forward(message);
        }
        
        if (!(message instanceof AnnounceMessage) || !message.isForwardOnly()) {
            processMessage(message);
        }
    }
    
    protected void forward(Message message)
    {
        if (!message.isForwardable()) return;
        // routing another channel
        message.setForwardOnly(false);
        Iterator<String> it = forwards.iterator();
        while (it.hasNext()) {
            String channelName = it.next();
            Channel channel = ElChatPlugin.getPlugin().getChannelManager().getChannel(channelName);
            if (channel == null || channel.equals(this)) continue;
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
            format = formatPlayer(format, (PlayerMessage) message);
            format = formatChannel(format, message);
            

            String textMessage = ((ChatMessage)message).getMessage();
            // IDSP対策のため全角スペースを半角スペースに置換
            textMessage = textMessage.replace("　", "　");

            format = format.replace("{message}", textMessage);

            format = format.replaceAll("&([a-z0-9])", "\u00A7$1");

            return format;
        } else if (message instanceof ChannelMessage) {
            String format = getChannelFormat();
            format = formatChannel(format, message);
            format = format.replace("{message}", ((ChannelMessage)message).getMessage());
            format = format.replaceAll("&([a-z0-9])", "\u00A7$1");
            return format;
        } else if (message instanceof PluginMessage) {
            String format = ((PluginMessage) message).getMessage();
            return format.replaceAll("&([a-z0-9])", "\u00A7$1");
        }
        return "";
    }
    
    public String formatPlayer(String format, PlayerMessage message)
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
        return format;
    }
    
    public String formatChannel(String format, Message message)
    {
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
    
    public String getChannelFormat()
    {
        if (channelFormat == null) return ElChatPlugin.getPlugin().getConfig().getString("global.channel-format");
        return channelFormat;
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
    
    public void addForward(Channel channel)
    {
        if (channel != null) {
            addForward(channel.getName());
        }
    }
    
    public void addForward(String channelName)
    {
        if (channelName != null && !forwards.contains(channelName)) {
            forwards.add(channelName);
        }
    }

    public List<String> getForwards() {
        return forwards;
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

    public boolean isForwardAnnounce() {
        return forwardAnnounce;
    }

    public void setForwardAnnounce(boolean forwardAnnounce) {
        this.forwardAnnounce = forwardAnnounce;
    }

    public abstract void join(ChatPlayer player);
    public abstract void quit(ChatPlayer player);
    public abstract void chat(ChatPlayer player, String message);
    
    public void broadcast(String message)
    {
        Message m = new ChannelMessage(message);
        sendMessage(m);
    }

    public void announce(String message)
    {
        Message m = new AnnounceMessage(message);
        m.setForwardable(forwardAnnounce);
        m.setForwardOnly(!announce);
        sendMessage(m);
    }
}
