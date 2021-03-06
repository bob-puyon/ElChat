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

package jp.commun.minecraft.elchat.message;

import jp.commun.minecraft.elchat.ChatPlayer;

public class ChatMessage extends PlayerMessage {
    protected String message;
    protected String converted;

    public ChatMessage(ChatPlayer player, String message) {
        super(player);
        this.message = message;
    }

    public ChatMessage(String playerName, String message) {
        super(playerName);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConvertedMessage() {
        return converted;
    }

    public void setConvertedMessage(String converted) {
        this.converted = converted;
    }
}
