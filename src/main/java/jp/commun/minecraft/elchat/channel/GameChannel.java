/*
 * Copyright 2012 ayunyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.commun.minecraft.elchat.channel;

import jp.commun.minecraft.elchat.ChatPlayer;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class GameChannel extends Channel {
    protected Map<String, ChatPlayer> players;
    protected int area = 0;
    protected boolean onlyWorld = false;
    protected boolean moderation = false;
    protected String owner;
    protected List<String> moderators;
    protected List<String> bans;
    protected List<String> mutes;
    protected String password;

    public GameChannel(String name) {
        super(name);
        players = new HashMap<String, ChatPlayer>();
        moderators = new ArrayList<String>();
        bans = new ArrayList<String>();
        mutes = new ArrayList<String>();
    }

    @Override
    public void loadConfig(ConfigurationSection section) {
        super.loadConfig(section);

        area = section.getInt("area", 0);
        onlyWorld = section.getBoolean("only-world", false);
        moderation = section.getBoolean("moderation", false);
        owner = section.getString("owner");
        if (section.contains("moderators")) {
            moderators = section.getStringList("moderatos");
        }
        if (section.contains("bans")) {
            bans = section.getStringList("bas");
        }
        if (section.contains("mutes")) {
            mutes = section.getStringList("mutes");
        }
        password = section.getString("password");
    }

    @Override
    public void saveConfig(ConfigurationSection section) {
        super.saveConfig(section);

        section.set("area", area);
        section.set("only-world", onlyWorld);
        section.set("moderation", moderation);
        section.set("owner", owner);
        section.set("moderator", moderators);
        section.set("bans", bans);
        section.set("mutes", mutes);
        section.set("password", password);
    }

    @Override
    public void processMessage(Message message) {
        String formattedMessage = formatMessage(message);
        // forwardをチェックしていないのでannounceが流れちゃう
        Iterator<ChatPlayer> it = players.values().iterator();
        while (it.hasNext()) {
            ChatPlayer recipient = it.next();
            ChatPlayer sender = null;
            if (message instanceof ChatMessage) {
                sender = ((ChatMessage) message).getPlayer();
            }
            if (onlyWorld && sender != null && !recipient.getPlayer().getWorld().equals(sender.getPlayer().getWorld()))
                continue;
            if (area != 0 && sender != null && recipient.getLocation().distance(sender.getLocation()) > area) continue;

            String channelNo = "";
            if (message.getChannel().equals(this)) {
                int no = recipient.getChannelNo(this);
                if (no != 0) {
                    channelNo = String.valueOf(no) + ". ";
                }
            }
            recipient.sendMessage(formattedMessage.replace("{channelno}", channelNo));
        }
    }

    @Override
    public void join(ChatPlayer player) {
        if (players.containsKey(player.getName())) return;

        if (bans.contains(player.getName())) {
            player.sendMessage("You are banned from that channel.");
            return;
        }

        if (password != null && ((owner != null && !player.getName().equals(owner)) || !moderators.contains(player.getName()))) {
            player.sendMessage("Incorrect channel password.");
            return;
        }

        players.put(player.getName(), player);
        player.addChannel(this);

        player.sendMessage("Joined Channel: [" + String.valueOf(player.getChannelNo(this)) + ". " + getTitle() + "]");

        announce(player.getName() + " joined channel.");
    }

    @Override
    public void quit(ChatPlayer player) {
        if (!players.containsKey(player.getName())) return;
        player.sendMessage("Left Channel: [" + String.valueOf(player.getChannelNo(this)) + ". " + getTitle() + "]");

        announce(player.getName() + " left channel.");

        players.remove(player.getName());
    }

    @Override
    public void chat(ChatPlayer player, String message) {
        if (!players.containsKey(player.getName())) {
            player.sendMessage("You're not on that channel.");
            return;
        }

        if (mutes.contains(player.getName()) || (isModeration() && !isOwner(player) && !isModerator(player))) {
            player.sendMessage("You do not have permission to speak.");
            return;
        }

        Message m = new ChatMessage(player, message);
        sendMessage(m);
    }

    public void kick(ChatPlayer player) {
        if (players.containsKey(player.getName())) {
            player.sendMessage("You are kicked from that channel.");
            quit(player);
            player.removeChannel(this);
        }
    }

    public void ban(ChatPlayer player) {
        if (!bans.contains(player.getName())) {
            bans.add(player.getName());

            // モデレータから削除
            if (moderators.contains(player.getName())) {
                moderators.remove(player.getName());
            }

            // オーナーから削除
            if (isOwner(player)) {
                setOwner(null);
            }

            if (players.containsKey(player.getName())) {
                player.sendMessage("You are banned from that channel.");
                quit(player);
                player.removeChannel(this);
            }
        }
    }

    public void unban(ChatPlayer player) {
        if (bans.contains(player.getName())) {
            bans.remove(player.getName());
        }
    }

    public void mute(ChatPlayer player) {
        if (!mutes.contains(player.getName())) {
            mutes.add(player.getName());
        }
    }

    public void unmute(ChatPlayer player) {
        if (mutes.contains(player.getName())) {
            mutes.remove(player.getName());
        }
    }

    public Map<String, ChatPlayer> getPlayers() {
        return players;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public boolean isOnlyWorld() {
        return onlyWorld;
    }

    public void setOnlyWorld(boolean onlyWorld) {
        this.onlyWorld = onlyWorld;
    }

    public boolean isModeration() {
        return moderation;
    }

    public void setModeration(boolean moderation) {
        this.moderation = moderation;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isOwner(ChatPlayer player) {
        return (owner != null && owner.equals(player.getName()));
    }


    public List<String> getModerators() {
        return moderators;
    }

    public boolean isModerator(ChatPlayer player) {
        return moderators.contains(player.getName());
    }

    public List<String> getBans() {
        return bans;
    }

    public List<String> getMutes() {
        return mutes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
