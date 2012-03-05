package jp.commun.minecraft.elchat.irc;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/05
 * Time: 20:11
 * To change this template use File | Settings | File Templates.
 */
public class Channel {
    private String name;

    public Channel(ConfigurationSection section) {
        this.name = section.getName();
    }

    public String getName() {
        return name;
    }
}
