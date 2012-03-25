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

package jp.commun.minecraft.elchat.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IRCCommandEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();
    private final String network;
    private final String channel;
    private final String nick;
    private final String commandName;
    private final String[] args;

    public IRCCommandEvent(String network, String channel, String nick, String commandName, String[] args) {
        this.network = network;
        this.channel = channel;
        this.nick = nick;
        this.commandName = commandName;
        this.args = args;
    }

    public String getNetwork() {
        return network;
    }

    public String getChannel() {
        return channel;
    }

    public String getNick() {
        return nick;
    }
    
    public String getCommandName()
    {
        return commandName;
    }

    public String[] getArgs()
    {
        return args;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
