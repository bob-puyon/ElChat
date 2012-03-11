package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.*;
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
    protected boolean autoJoin;
    protected String messageFormat;
    protected String joinFormat;
    protected String quitFormat;
    protected boolean romaToHira;
    protected List<String> routes;

    public Channel(String name)
    {
        this.name = name;
        this.routes = new ArrayList<String>();
    }
    
    public void loadConfig(ConfigurationSection section)
    {
        this.name = section.getName();
        if (section.contains("title")) this.title = section.getString("title");
        if (section.contains("auto-join")) this.autoJoin = section.getBoolean("auto-join", false);
        if (section.contains("message-format")) messageFormat = section.getString("message-format", null);
        if (section.contains("join-format")) joinFormat = section.getString("join-format", null);
        if (section.contains("quit-format")) quitFormat = section.getString("quit-format", null);
        if (section.contains("roma-to-hira")) romaToHira = section.getBoolean("roma-to-hira", false);
        if (section.contains("routes")) {
            routes = section.getStringList("routes");
        }
    }
    
    public void saveConfig(ConfigurationSection section)
    {
        section.set("title", title);
        section.set("auto-join", autoJoin);
        section.set("message-format", messageFormat);
        section.set("join-format", joinFormat);
        section.set("quit-format", quitFormat);
        section.set("roma-to-hira", romaToHira);
        section.set("routes", routes);
    }
    
    public void sendMessage(Message message)
    {
        // routing another channel
        if (message.getChannel() == null) {
            message.setChannel(this);
            Iterator<String> it = routes.iterator();
            while (it.hasNext()) {
                String channelName = it.next();
                Log.info("routing nexthop check:" + channelName);
                Channel channel = ElChatPlugin.getPlugin().getChannelManager().getChannel(channelName);
                if (channel == null || channel.equals(this)) continue;
                Log.info("nexthop:" + channel.getName());
                channel.sendMessage(message);
            }
        }
    }
    
    @Override
    public boolean equals(Object o)
    {
        return (o instanceof Channel && ((Channel) o).getName().equals(getName()));
    }
    
    public String formatMessage(Message message)
    {
        String format = getMessageFormat();

        ChatPlayer sender = message.getPlayer();
        String userPrefix = "";
        String userSuffix = "";
        if (sender != null) {
            format = format.replace("%world", sender.getPlayer().getWorld().getName());
            format = format.replace("%player", sender.getName());
            PermissionManager permissionManager = ElChatPlugin.getPlugin().getPermissionsExManager();
            if (permissionManager != null) {
                PermissionUser user = permissionManager.getUser(sender.getPlayer());
                if (user != null) {
                    if (user.getPrefix() != null) userPrefix = user.getPrefix();
                    if (user.getSuffix() != null) userSuffix = user.getSuffix();
                }
            }
        } else {
            format = format.replace("%world", "");
            format = format.replace("%player", message.getPlayerName());
        }
        format = format.replace("%prefix", userPrefix);
        format = format.replace("%suffix", userSuffix);
        format = format.replace("%channel", message.getChannel().getTitle());

        String textMessage = message.getMessage();
        // IDSP対策のため全角スペースを半角スペースに置換
        textMessage = textMessage.replace("　", "　");
        if (romaToHira) {
            String cleanMessage = textMessage.replaceAll("&([a-z0-9])", "");
            if (!RomaToHira.hasHiragana(cleanMessage)) {
                String hiraMessage = RomaToHira.convert(cleanMessage);
                if (!hiraMessage.equals(cleanMessage)) {
                    textMessage += " &f(" + hiraMessage + ")";
                }
            }
        }
        format = format.replace("%message", textMessage);

        format = format.replaceAll("&([a-z0-9])", "\u00A7$1");

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

    public String getTitle() {
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

    public abstract void join(ChatPlayer player);
    public abstract void quit(ChatPlayer player);
}
