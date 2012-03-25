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

package jp.commun.minecraft.elchat.irc;

import java.util.HashMap;
import java.util.Map;

public class IRCColor {
    private static final Map<String, String> COLOR_MAP = new HashMap<String, String>() {{
        //put("00", "f"); // white
        put("01", "0"); // black
        put("02", "1"); // dark blue
        put("03", "2"); // dark green
        put("04", "4"); // red
        put("05", "8"); // brown
        put("06", "5"); // purple
        put("07", "6"); // olive
        put("08", "e"); // yellow
        put("09", "a"); // green
        put("10", "3"); // teal
        put("11", "b"); // cyan
        put("12", "9"); // blue
        put("13", "d"); // magenta
        put("14", "c"); // dark gray
        put("15", "7"); // light gray
    }};

    public static String toIRC(String text) {
        for (String ircColor : COLOR_MAP.keySet()) {
            text = text.replace("\u00A7" + COLOR_MAP.get(ircColor), "\u0003" + ircColor);
        }
        // white to normal
        text = text.replace("\u00A7f", "\u000f");
        return text;
    }

    public static String toGame(String text) {
        for (String ircColor : COLOR_MAP.keySet()) {
            text = text.replace("\u0003" + ircColor, "\u00A7" + COLOR_MAP.get(ircColor));
        }
        // normal to white
        text = text.replace("\u000f", "\u00A7f");
        return text;
    }
}
