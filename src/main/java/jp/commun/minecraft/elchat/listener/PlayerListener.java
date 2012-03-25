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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    private final ElChatPlugin plugin;

    public PlayerListener(ElChatPlugin instance)
    {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        plugin.getPlayerManager().onPlayerJoin(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        plugin.getPlayerManager().onPlayerQuit(event);
    }

    @EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		
        plugin.getPlayerManager().onPlayerChat(event);
	}
}
