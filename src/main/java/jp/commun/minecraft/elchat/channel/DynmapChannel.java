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
import jp.commun.minecraft.elchat.ElChatPlugin;
import jp.commun.minecraft.elchat.message.ChatMessage;
import jp.commun.minecraft.elchat.message.Message;

import org.dynmap.DynmapAPI;

public class DynmapChannel extends Channel {
    public DynmapChannel(String name) {
        super(name);

        this.type = "dynmap";
    }

    @Override
    public void processMessage(Message message) {

        if (!message.getChannel().equals(this)) {
            DynmapAPI dynmapAPI = ElChatPlugin.getPlugin().getDynmapAPI();
            if (dynmapAPI != null) {
            	if( message instanceof ChatMessage ){
            		String hira = ((ChatMessage)message).getConvertedMessage();
            		if(hira != null){
            			dynmapAPI.sendBroadcastToWeb(null, (" [日本語変換] : " + hira));
            		}
            	}else{
            		dynmapAPI.sendBroadcastToWeb(null, formatMessage(message));
            	}
            }
        }
    }

    @Override
    public void join(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void quit(ChatPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void chat(ChatPlayer player, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void broadcast(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
