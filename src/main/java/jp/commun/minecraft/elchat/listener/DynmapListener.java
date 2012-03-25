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

package jp.commun.minecraft.elchat.listener;

import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.channel.Channel;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.dynmap.DynmapWebChatEvent;

public class DynmapListener implements Listener {
    private final ElChatPlugin plugin;

    public DynmapListener(ElChatPlugin instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDynmapWebChat(DynmapWebChatEvent event) {
        if (event.isCancelled()) return;

        Message message = new ChatMessage(event.getName(), event.getMessage());
        Channel channel = plugin.getChannelManager().getChannel("dynmap");
        if (channel != null) {
            channel.sendMessage(message);

            event.setCancelled(true);
        }
    }
}
